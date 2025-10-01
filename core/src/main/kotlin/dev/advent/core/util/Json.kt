package dev.advent.core.util

import kotlinx.serialization.json.*

object Json {
    /** Convert primitive Kotlin types to JsonElement (strings, numbers, booleans) */
    fun primitives(value: Any?): JsonElement = when (value) {
        null -> JsonNull
        is JsonElement -> value
        is String -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is Map<*, *> -> buildJsonObject {
            value.forEach { (k, v) -> if (k is String) put(k, primitives(v)) }
        }
        is List<*> -> buildJsonArray { value.forEach { add(primitives(it)) } }
        else -> JsonPrimitive(value.toString())
    }
}