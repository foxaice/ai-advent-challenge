package dev.advent.day04.web

import dev.advent.core.agent.Agent
import dev.advent.core.agent.ToolRegistry
import dev.advent.core.llm.GenerationConfig
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
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8084
    logger.info("Starting Day 4 web server on port {}", port)
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
                logger.info("Received chat request: message={}, temperature={}", req.message, req.temperature)

                val responseText = try {
                    val provider = GeminiProvider()
                    val model = System.getenv("GEMINI_MODEL") ?: "gemini-2.0-flash"
                    logger.debug("Creating agent with model: {} and temperature: {}", model, req.temperature)

                    val tools = ToolRegistry(emptyList())
                    val agent = Agent(
                        provider = provider,
                        model = model,
                        tools = tools,
                        systemInstruction = """
                            Ты — умный ассистент.
                            Отвечай кратко, информативно и по делу.
                            Твоя задача — продемонстрировать различия в ответах при разных температурах.
                        """.trimIndent(),
                        config = GenerationConfig(
                            temperature = req.temperature
                        )
                    )

                    val result = agent.chatOnce(req.message)
                    logger.info("Chat completed with temperature {}, result: {}", req.temperature, result)
                    result
                } catch (e: Exception) {
                    logger.error("Error processing chat request", e)
                    "Ошибка: ${e.message}"
                }
                call.respond(ChatResponse(responseText))
            }
        }
    }
}

@Serializable
data class ChatRequest(val message: String, val temperature: Double = 0.7)
@Serializable
data class ChatResponse(val reply: String)