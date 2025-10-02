package dev.advent.core.util

/**
 * Утилита для извлечения JSON из разных форматов ответов LLM
 */
object JsonExtractor {
    /**
     * Извлекает JSON из текста, который может содержать:
     * - Чистый JSON
     * - JSON в markdown code block (```json ... ```)
     * - JSON в обычном code block (``` ... ```)
     * - JSON с текстом до/после
     */
    fun extract(text: String): String {
        val trimmed = text.trim()

        // 1. Попытка найти JSON в markdown code block с указанием языка
        val markdownJsonPattern = """```json\s*([\s\S]*?)```""".toRegex()
        markdownJsonPattern.find(trimmed)?.let { match ->
            return match.groupValues[1].trim()
        }

        // 2. Попытка найти JSON в обычном code block
        val codeBlockPattern = """```\s*([\s\S]*?)```""".toRegex()
        codeBlockPattern.find(trimmed)?.let { match ->
            val content = match.groupValues[1].trim()
            // Проверяем, что это похоже на JSON (начинается с { или [)
            if (content.startsWith("{") || content.startsWith("[")) {
                return content
            }
        }

        // 3. Определяем, что идёт раньше: { или [
        val firstBrace = trimmed.indexOf('{')
        val firstBracket = trimmed.indexOf('[')

        // Если оба найдены, выбираем тот что раньше
        when {
            firstBrace != -1 && (firstBracket == -1 || firstBrace < firstBracket) -> {
                // JSON объект идёт первым
                val lastBrace = trimmed.lastIndexOf('}')
                if (lastBrace != -1 && lastBrace > firstBrace) {
                    return trimmed.substring(firstBrace, lastBrace + 1)
                }
            }
            firstBracket != -1 -> {
                // JSON массив идёт первым
                val lastBracket = trimmed.lastIndexOf(']')
                if (lastBracket != -1 && lastBracket > firstBracket) {
                    return trimmed.substring(firstBracket, lastBracket + 1)
                }
            }
        }

        // 5. Если ничего не нашли, возвращаем оригинальный текст
        return trimmed
    }

    /**
     * Проверяет, является ли текст валидным JSON
     */
    fun isValidJson(text: String): Boolean {
        return try {
            kotlinx.serialization.json.Json.parseToJsonElement(text)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Извлекает и валидирует JSON. Возвращает null если не удалось извлечь валидный JSON
     */
    fun extractAndValidate(text: String): String? {
        val extracted = extract(text)
        return if (isValidJson(extracted)) extracted else null
    }
}