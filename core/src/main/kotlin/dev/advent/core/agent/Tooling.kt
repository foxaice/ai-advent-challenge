package dev.advent.core.agent

import dev.advent.core.llm.ToolJsonSchema

/** Execute local tools based on name + args */
interface ToolExecutor {
    val name: String
    suspend fun invoke(args: Map<String, Any?>): Map<String, Any?>

    /** JSON schema declaration passed to models */
    fun declaration(): ToolJsonSchema
}

class ToolRegistry(private val tools: List<ToolExecutor>) {
    fun declarations(): List<ToolJsonSchema> = tools.map { it.declaration() }
    fun get(name: String): ToolExecutor? = tools.firstOrNull { it.name == name }
}