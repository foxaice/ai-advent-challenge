package dev.advent.core.tools

import dev.advent.core.agent.ToolExecutor
import dev.advent.core.llm.ToolJsonSchema
import kotlinx.serialization.json.*
import javax.script.ScriptEngineManager

/** very naive calculator using JavaScript engine */
class CalcTool : ToolExecutor {
    override val name: String = "calc"

    override fun declaration(): ToolJsonSchema {
        val schema = buildJsonObject {
            put("type", "object")
            put("properties", buildJsonObject {
                put("expression", buildJsonObject {
                    put("type", "string")
                    put("description", "Арифметическое выражение, например: 2+2*10")
                })
            })
            put("required", buildJsonArray { add(JsonPrimitive("expression")) })
        }
        return ToolJsonSchema(
            name = name,
            description = "Считает арифметическое выражение и возвращает числовой результат.",
            parameters = mapOf(
                "type" to JsonPrimitive("object"),
                "properties" to schema["properties"]!!,
                "required" to schema["required"]!!
            )
        )
    }

    override suspend fun invoke(args: Map<String, Any?>): Map<String, Any?> {
        val expr = (args["expression"] as? String)?.take(200) ?: return mapOf("error" to "expression is required")
        val engine = ScriptEngineManager().getEngineByExtension("kts")
            ?: ScriptEngineManager().getEngineByName("JavaScript")
            ?: ScriptEngineManager().getEngineByName("js")
            ?: ScriptEngineManager().getEngineByName("nashorn")
            ?: return mapOf("error" to "No script engine available (tried: kts, JavaScript, js, nashorn)")

        // Convert ^ and ** operators to Math.pow()
        val jsExpr = expr
            .replace(Regex("""(\d+(?:\.\d+)?)\s*\*\*\s*(\d+(?:\.\d+)?)""")) { "Math.pow(${it.groupValues[1]}, ${it.groupValues[2]})" }
            .replace(Regex("""(\d+(?:\.\d+)?)\s*\^\s*(\d+(?:\.\d+)?)""")) { "Math.pow(${it.groupValues[1]}, ${it.groupValues[2]})" }

        val result = try {
            engine.eval(jsExpr).toString()
        } catch (e: Exception) {
            return mapOf("error" to (e.message ?: "calc error"))
        }
        return mapOf("result" to result)
    }
}