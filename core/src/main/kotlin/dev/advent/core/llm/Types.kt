package dev.advent.core.llm

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
enum class Role { system, user, model }

@Serializable
sealed class Part {
    @Serializable
    data class Text(val text: String): Part()

    @Serializable
    data class FunctionCall(val name: String, val args: Map<String, JsonElement>): Part()

    @Serializable
    data class FunctionResponse(val name: String, val response: Map<String, JsonElement>): Part()
}

@Serializable
data class Content(
    val role: Role,
    val parts: List<Part>
)

@Serializable
data class ChatMessage(val role: Role, val text: String)

@Serializable
data class ToolJsonSchema(
    val name: String,
    val description: String,
    val parameters: Map<String, JsonElement> // OpenAPI subset / JSON schema
)

@Serializable
enum class ResponseFormat {
    JSON,      // Structured JSON response
    MARKDOWN,  // Markdown formatted response
    PLAIN      // Plain text response
}

@Serializable
data class GenerationConfig(
    val temperature: Double? = null,
    val topK: Int? = null,
    val topP: Double? = null,
    val responseFormat: ResponseFormat? = null,
    val responseSchema: String? = null  // JSON schema example for the response
)

// Response wrapper unified for providers
sealed class LlmAnswer {
    data class Text(val text: String): LlmAnswer()
    data class ToolCall(val name: String, val args: Map<String, Any?>): LlmAnswer()
}