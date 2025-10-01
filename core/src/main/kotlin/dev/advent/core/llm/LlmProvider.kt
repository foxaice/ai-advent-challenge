package dev.advent.core.llm

interface LlmProvider {
    val name: String

    /**
     * Send a single-turn request. The provider may return either a text answer or a tool call suggestion.
     * Implementations should not execute tools. They only translate the provider-specific response to [LlmAnswer].
     */
    suspend fun generate(
        model: String,
        systemInstruction: String?,
        contents: List<Content>,
        tools: List<ToolJsonSchema> = emptyList(),
        config: GenerationConfig = GenerationConfig()
    ): LlmAnswer
}