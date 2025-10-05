package dev.advent.day03.web

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
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8083
    logger.info("Starting Day 3 web server on port {}", port)
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)
}

fun Application.module() {
    val logger = LoggerFactory.getLogger("WebModule")

    install(CORS) { anyHost() }
    install(ContentNegotiation) { json() }

    // Хранилище истории диалогов по sessionId
    val sessions = mutableMapOf<String, MutableList<ConversationMessage>>()

    routing {
        // Static UI
        staticIndex()

        route("/api") {
            post("/chat") {
                val req = call.receive<ChatRequest>()
                logger.info("Received chat request: sessionId={}, message={}", req.sessionId, req.message)

                try {
                    val sessionId = req.sessionId
                    val history = sessions.getOrPut(sessionId) { mutableListOf() }

                    // Добавляем сообщение пользователя в историю
                    history.add(ConversationMessage("user", req.message))

                    val provider = GeminiProvider()
                    val model = System.getenv("GEMINI_MODEL") ?: "gemini-2.0-flash"

                    val tools = ToolRegistry(emptyList())
                    val agent = Agent(
                        provider = provider,
                        model = model,
                        tools = tools,
                        systemInstruction = buildSystemPrompt(),
                        config = GenerationConfig(temperature = 0.7)
                    )

                    // Формируем полный контекст диалога
                    val conversationContext = buildConversationContext(history)
                    val response = agent.chatOnce(conversationContext)

                    logger.info("Agent response: {}", response)

                    // Добавляем ответ агента в историю
                    history.add(ConversationMessage("assistant", response))

                    // Проверяем, завершил ли агент сбор требований
                    val isComplete = checkIfComplete(response)

                    call.respond(ChatResponse(
                        reply = response,
                        isComplete = isComplete
                    ))
                } catch (e: Exception) {
                    logger.error("Error processing chat request", e)
                    call.respond(ChatResponse(
                        reply = "Ошибка: ${e.message}",
                        isComplete = false
                    ))
                }
            }

            post("/reset") {
                val req = call.receive<ResetRequest>()
                sessions.remove(req.sessionId)
                logger.info("Session reset: {}", req.sessionId)
                call.respond(mapOf("status" to "ok"))
            }
        }
    }
}

private fun buildSystemPrompt(): String = """
Ты — волшебный сказочник, который создаёт описания сказочных уток! 🦆✨

Твоя задача:
1. Задавать пользователю интересные вопросы о его сказочной утке
2. Собирать информацию о:
   - Внешности утки (цвет перьев, размер, особые черты)
   - Характере и личности утки
   - Волшебных способностях или талантах
   - Месте обитания (волшебный пруд, облака, подводное царство и т.д.)
   - Любимых занятиях и хобби
   - Друзьях и приключениях
3. Когда соберешь достаточно информации (минимум 5-6 деталей), спроси пользователя: "У меня достаточно информации! Создать описание твоей сказочной утки?"
4. Если пользователь согласен, создай красочное сказочное описание в следующем формате:

===СКАЗОЧНАЯ УТКА===

## 🦆 Имя утки
[придумай волшебное имя на основе характеристик]

## ✨ Внешность
[яркое описание внешнего вида]

## 💫 Характер
[описание личности и темперамента]

## 🪄 Волшебные способности
[особые таланты и магические умения]

## 🏰 Место обитания
[описание сказочного дома]

## 🎭 Любимые занятия
[хобби и увлечения]

## 📖 Сказочная история
[короткая история о приключениях утки, 2-3 абзаца]

===КОНЕЦ СКАЗКИ===

ВАЖНО:
- Создавай описание только после согласия пользователя!
- Задавай по 1-2 вопроса за раз, чтобы беседа была увлекательной
- Будь креативным, добрым и весёлым
- Используй эмодзи, чтобы сделать диалог более живым
- Если пользователь даёт мало деталей, помоги ему своими предложениями
""".trimIndent()

private fun buildConversationContext(history: List<ConversationMessage>): String {
    return history.joinToString("\n\n") { msg ->
        when (msg.role) {
            "user" -> "Пользователь: ${msg.content}"
            "assistant" -> "Ассистент: ${msg.content}"
            else -> msg.content
        }
    }
}

private fun checkIfComplete(response: String): Boolean {
    // Проверяем, содержит ли ответ финальное описание сказочной утки
    return response.contains("===СКАЗОЧНАЯ УТКА===") &&
           response.contains("===КОНЕЦ СКАЗКИ===")
}

@Serializable
data class ConversationMessage(
    val role: String,
    val content: String
)

@Serializable
data class ChatRequest(
    val sessionId: String,
    val message: String
)

@Serializable
data class ChatResponse(
    val reply: String,
    val isComplete: Boolean
)

@Serializable
data class ResetRequest(val sessionId: String)