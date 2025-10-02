package dev.advent.day02.web

import dev.advent.core.agent.Agent
import dev.advent.core.agent.ToolRegistry
import dev.advent.core.llm.GenerationConfig
import dev.advent.core.llm.ResponseFormat
import dev.advent.core.util.JsonExtractor
import dev.advent.providers.gemini.GeminiProvider
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory

fun main() {
    val logger = LoggerFactory.getLogger("WebServer")
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    logger.info("Starting Day 2 web server on port {}", port)
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)
}

fun Application.module() {
    val logger = LoggerFactory.getLogger("WebModule")

    install(CORS) { anyHost() }
    install(ContentNegotiation) { json() }

    routing {
        // Static UI
        staticIndex()

        route("/api") {
            post("/chat") {
                val req = call.receive<ChatRequest>()
                logger.info("Received chat request: message={}", req.message)

                val responseText = try {
                    val provider = GeminiProvider()
                    val model = System.getenv("GEMINI_MODEL") ?: "gemini-2.0-flash"
                    logger.debug("Creating agent with model: {}", model)

                    // Пример JSON схемы для ответа
                    val jsonSchema = """
                    {
                      "answer": "ответ на вопрос пользователя (string)",
                      "confidence": "уверенность в ответе от 0 до 100 (number)",
                      "sources": "список источников или пояснений (array of strings)"
                    }
                    """.trimIndent()

                    val tools = ToolRegistry(emptyList())
                    val agent = Agent(
                        provider = provider,
                        model = model,
                        tools = tools,
                        systemInstruction = "Ты — умный ассистент. Отвечай кратко и по делу.",
                        config = GenerationConfig(
                            temperature = 0.5,
                            responseFormat = ResponseFormat.JSON,
                            responseSchema = jsonSchema
                        )
                    )

                    val rawResult = agent.chatOnce(req.message)
                    logger.info("Chat completed, raw result: {}", rawResult)

                    // Извлекаем JSON из ответа (может быть в markdown блоке)
                    val extractedJson = JsonExtractor.extract(rawResult)
                    logger.debug("Extracted JSON: {}", extractedJson)

                    // Валидируем что это действительно JSON
                    if (JsonExtractor.isValidJson(extractedJson)) {
                        logger.info("Valid JSON extracted successfully")
                        extractedJson
                    } else {
                        logger.warn("Failed to extract valid JSON, returning fallback")
                        """{"answer":"${rawResult.replace("\"", "\\\"")}","confidence":50,"sources":["raw response"]}"""
                    }
                } catch (e: Exception) {
                    logger.error("Error processing chat request", e)
                    """{"answer":"Ошибка: ${e.message}","confidence":0,"sources":[]}"""
                }
                call.respond(ChatResponse(responseText))
            }
        }
    }
}

@Serializable
data class ChatRequest(val message: String)
@Serializable
data class ChatResponse(val reply: String)