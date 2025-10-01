package dev.advent.providers.gemini

import dev.advent.core.llm.*
import dev.advent.core.util.Json
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory

class GeminiProvider(
    private val apiKey: String = System.getenv("GEMINI_API_KEY") ?: error("GEMINI_API_KEY is not set"),
    private val endpoint: String = "https://generativelanguage.googleapis.com/v1beta"
) : LlmProvider {
    override val name: String = "gemini"
    private val logger = LoggerFactory.getLogger(GeminiProvider::class.java)

    private val prettyJson = kotlinx.serialization.json.Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true })
        }
    }

    override suspend fun generate(
        model: String,
        systemInstruction: String?,
        contents: List<Content>,
        tools: List<ToolJsonSchema>,
        config: GenerationConfig
    ): LlmAnswer {
        logger.debug("Generating content: model={}, toolsCount={}, contentsCount={}", model, tools.size, contents.size)

        val request = GeminiGenerateContentRequest(
            contents = contents.map { it.toGemini() },
            tools = if (tools.isNotEmpty()) listOf(GeminiTools(functionDeclarations = tools.map { it.toGemini() })) else null,
            systemInstruction = systemInstruction?.let { GeminiContent(role = "user", parts = listOf(GeminiPart(text = it))) },
            generationConfig = GeminiGenerationConfig(temperature = config.temperature, topK = config.topK, topP = config.topP)
        )

        val url = "$endpoint/models/$model:generateContent?key=***"
        logger.debug("Sending request to Gemini API: url={}", url)
        logger.info("Raw Gemini request:\n{}", prettyJson.encodeToString(GeminiGenerateContentRequest.serializer(), request))

        val response: GeminiGenerateContentResponse = try {
            client.post("$endpoint/models/$model:generateContent?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        } catch (e: Exception) {
            logger.error("Failed to call Gemini API", e)
            throw e
        }

        logger.info("Raw Gemini response:\n{}", prettyJson.encodeToString(GeminiGenerateContentResponse.serializer(), response))
        logger.debug("Received response: candidatesCount={}", response.candidates?.size ?: 0)

        // Happy path: either text or functionCall in first candidate
        val candidate = response.candidates?.firstOrNull()
        if (candidate == null) {
            logger.warn("No candidates in response, promptFeedback={}", response.promptFeedback)
            return LlmAnswer.Text(response.promptFeedback?.toString() ?: "(empty)")
        }

        val parts = candidate.content?.parts ?: emptyList()
        logger.debug("Candidate parts count: {}", parts.size)

        // Prefer function call if present
        parts.firstOrNull { it.functionCall != null }?.functionCall?.let { fc ->
            logger.info("Function call detected: name={}", fc.name)
            val args = fc.args ?: buildJsonObject { }
            return LlmAnswer.ToolCall(fc.name ?: "", args.toMap())
        }

        // Otherwise gather text
        val text = parts.joinToString("") { it.text ?: "" }.ifBlank { candidate.finishMessage() }
        logger.debug("Returning text response, length={}", text.length)
        return LlmAnswer.Text(text)
    }

    // --- DTOs + mappers ---
    @Serializable
    data class GeminiGenerateContentRequest(
        val contents: List<GeminiContent>,
        val tools: List<GeminiTools>? = null,
        val systemInstruction: GeminiContent? = null,
        val generationConfig: GeminiGenerationConfig? = null
    )

    @Serializable
    data class GeminiTools(
        val functionDeclarations: List<GeminiFunctionDeclaration>
    )

    @Serializable
    data class GeminiFunctionDeclaration(
        val name: String,
        val description: String,
        val parameters: JsonObject? = null
    )

    @Serializable
    data class GeminiGenerationConfig(
        val temperature: Double? = null,
        val topK: Int? = null,
        val topP: Double? = null
    )

    @Serializable
    data class GeminiContent(
        val role: String? = null,
        val parts: List<GeminiPart> = emptyList()
    )

    @Serializable
    data class GeminiPart(
        val text: String? = null,
        val functionCall: GeminiFunctionCall? = null,
        val functionResponse: GeminiFunctionResponse? = null,
        // thoughtSignature is present sometimes, we ignore
    )

    @Serializable
    data class GeminiFunctionCall(
        val name: String? = null,
        val args: JsonObject? = null
    )

    @Serializable
    data class GeminiFunctionResponse(
        val name: String? = null,
        val response: JsonObject? = null
    )

    @Serializable
    data class GeminiGenerateContentResponse(
        val candidates: List<GeminiCandidate>? = null,
        val promptFeedback: JsonObject? = null
    )

    @Serializable
    data class GeminiCandidate(
        val content: GeminiContent? = null,
        val finishReason: String? = null,
        val safetyRatings: List<JsonObject>? = null
    )

    private fun GeminiCandidate.finishMessage(): String = when (finishReason) {
        null -> ""
        else -> "(finish=$finishReason)"
    }

    private fun Content.toGemini(): GeminiContent = GeminiContent(
        role = this.role.name,
        parts = this.parts.map { it.toGeminiPart() }
    )

    private fun Part.toGeminiPart(): GeminiPart = when (this) {
        is Part.Text -> GeminiPart(text = this.text)
        is Part.FunctionCall -> GeminiPart(functionCall = GeminiFunctionCall(this.name, Json.primitives(this.args).jsonObject))
        is Part.FunctionResponse -> GeminiPart(functionResponse = GeminiFunctionResponse(this.name, Json.primitives(this.response).jsonObject))
    }

    private fun ToolJsonSchema.toGemini(): GeminiFunctionDeclaration = GeminiFunctionDeclaration(
        name = name,
        description = description,
        parameters = JsonObject(parameters)
    )

    private fun JsonObject.toMap(): Map<String, Any?> = this.mapValues { (_, v) ->
        when (v) {
            is JsonPrimitive -> if (v.isString) v.content else v.booleanOrNull ?: v.longOrNull ?: v.doubleOrNull ?: v.content
            is JsonObject -> v.toMap()
            is JsonArray -> v.map { je -> (je as? JsonPrimitive)?.content ?: je.toString() }
        }
    }
}