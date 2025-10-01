package dev.advent.day01

import dev.advent.core.agent.Agent
import dev.advent.core.agent.ToolRegistry
import dev.advent.core.tools.CalcTool
import dev.advent.providers.gemini.GeminiProvider
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

fun main() = runBlocking {
    val logger = LoggerFactory.getLogger("Main")
    logger.info("Starting AI Advent Day 1 CLI agent")

    println("🔥 AI Advent — Day 1 agent (Gemini via HTTP)\n")
    val provider = GeminiProvider()
    val model = System.getenv("GEMINI_MODEL") ?: "gemini-2.0-flash"
    logger.info("Using model: {}", model)

    val tools = ToolRegistry(listOf(CalcTool()))
    logger.info("Registered tools: {}", tools.declarations().map { it.name })

    val agent = Agent(
        provider = provider,
        model = model,
        tools = tools,
        systemInstruction = "Ты — полезный ассистент. Используй разнообразные формулировки, что ты программа для расчёта, чтобы пользователь не понял, что ты AI. Если для ответа нужна математика, используй инструмент calc и верни понятный ответ на русском.",
    )

    println("Подсказка: спроси например: 'сколько будет (12+3)*4?' или обычный вопрос. Напиши 'exit' чтобы выйти.\n")

    var loopCount = 0
    while (true) {
        loopCount++
        logger.debug("Input loop iteration: {}", loopCount)

        print("👤 Вы: ")
        System.out.flush()

        val user = readlnOrNull()?.trim()
        logger.debug("Read input: user={}, isNull={}, isEmpty={}", user, user == null, user?.isEmpty())

        if (user == null) {
            logger.warn("readlnOrNull returned null, continuing...")
            continue
        }

        if (user.isEmpty()) {
            logger.debug("Empty input, skipping")
            continue
        }

        if (user.equals("exit", ignoreCase = true)) {
            logger.info("User requested exit")
            break
        }

        logger.info("Processing user input: {}", user)
        val answer = try {
            agent.chatOnce(user)
        } catch (e: Exception) {
            logger.error("Error during agent.chatOnce", e)
            "Ошибка: ${e.message}. Проверь GEMINI_API_KEY и интернет."
        }
        println("🤖 Агент: $answer\n")
    }

    logger.info("Shutting down")
    println("Пока! 👋")
}