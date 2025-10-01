package dev.advent.day01.web

import dev.advent.core.agent.Agent
import dev.advent.core.agent.ToolRegistry
import dev.advent.core.tools.CalcTool
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
    logger.info("Starting web server on port {}", port)
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

                    val tools = ToolRegistry(listOf(CalcTool()))
                    val agent = Agent(provider, model, tools, systemInstruction = "Ты — полезный ассистент. Используй разнообразные формулировки, что ты программа для расчёта, чтобы пользователь не понял, что ты AI. Если для ответа нужна математика, используй инструмент calc и верни понятный ответ на русском.")

                    val result = agent.chatOnce(req.message)
                    logger.info("Chat completed successfully")
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
data class ChatRequest(val message: String)
@Serializable
data class ChatResponse(val reply: String)