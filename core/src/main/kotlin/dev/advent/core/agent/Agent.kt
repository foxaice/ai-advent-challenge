package dev.advent.core.agent

import dev.advent.core.llm.*
import org.slf4j.LoggerFactory

/**
 * Simple agent loop: ask model; if it requests a tool, execute tool; send tool response back and get final text.
 */
class Agent(
    private val provider: LlmProvider,
    private val model: String,
    private val tools: ToolRegistry,
    private val systemInstruction: String? = null,
    private val config: GenerationConfig = GenerationConfig(temperature = 0.7)
) {
    private val logger = LoggerFactory.getLogger(Agent::class.java)

    suspend fun chatOnce(userText: String): String {
        logger.debug("User input: {}", userText)

        // 1) ask model with tool declarations
        logger.debug("Calling LLM provider: model={}, tools={}", model, tools.declarations().map { it.name })
        val initial = provider.generate(
            model = model,
            systemInstruction = systemInstruction,
            contents = listOf(
                Content(Role.user, listOf(Part.Text(userText)))
            ),
            tools = tools.declarations(),
            config = config
        )
        logger.debug("LLM initial response: {}", initial)

        return when (initial) {
            is LlmAnswer.Text -> {
                logger.debug("Returning text response: {}", initial.text)
                initial.text
            }
            is LlmAnswer.ToolCall -> {
                logger.info("Tool call requested: name={}, args={}", initial.name, initial.args)
                val tool = tools.get(initial.name)
                if (tool == null) {
                    logger.warn("Tool not found: {}", initial.name)
                    return "(модель попросила инструмент '${initial.name}', но он не зарегистрирован)"
                }
                val toolResult = tool.invoke(initial.args)
                logger.info("Tool execution result: {}", toolResult)

                // Send back function call + function response as follow-up turn
                logger.debug("Sending tool result back to LLM")
                val final = provider.generate(
                    model = model,
                    systemInstruction = systemInstruction,
                    contents = listOf(
                        Content(
                            role = Role.model,
                            parts = listOf(Part.FunctionCall(name = initial.name, args = initial.args.mapValues { dev.advent.core.util.Json.primitives(it.value) }))
                        ),
                        Content(
                            role = Role.user,
                            parts = listOf(Part.FunctionResponse(name = initial.name, response = toolResult.mapValues { dev.advent.core.util.Json.primitives(it.value) }))
                        )
                    ),
                    tools = tools.declarations(),
                    config = config
                )
                logger.debug("LLM final response after tool: {}", final)

                when (final) {
                    is LlmAnswer.Text -> {
                        logger.debug("Returning final text: {}", final.text)
                        final.text
                    }
                    is LlmAnswer.ToolCall -> {
                        logger.warn("LLM requested another tool call: {}", final.name)
                        "(модель снова запросила инструмент ${final.name} — для простоты завершаем тут)"
                    }
                }
            }
        }
    }
}