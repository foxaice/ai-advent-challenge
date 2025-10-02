package dev.advent.core.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class JsonExtractorTest {

    @Test
    fun `extract pure JSON`() {
        val input = """{"answer":"Привет","confidence":95,"sources":["test"]}"""
        val result = JsonExtractor.extract(input)
        assertEquals(input, result)
        assertTrue(JsonExtractor.isValidJson(result))
    }

    @Test
    fun `extract JSON from markdown json block`() {
        val input = """
            ```json
            {
              "answer": "Привет",
              "confidence": 95,
              "sources": ["test"]
            }
            ```
        """.trimIndent()

        val result = JsonExtractor.extract(input)
        val expected = """
            {
              "answer": "Привет",
              "confidence": 95,
              "sources": ["test"]
            }
        """.trimIndent()

        assertEquals(expected, result)
        assertTrue(JsonExtractor.isValidJson(result))
    }

    @Test
    fun `extract JSON from plain code block`() {
        val input = """
            ```
            {"answer":"Тест","confidence":85,"sources":[]}
            ```
        """.trimIndent()

        val result = JsonExtractor.extract(input)
        assertEquals("""{"answer":"Тест","confidence":85,"sources":[]}""", result)
        assertTrue(JsonExtractor.isValidJson(result))
    }

    @Test
    fun `extract JSON with text before and after`() {
        val input = """
            Вот ответ на ваш вопрос:

            {"answer":"Ответ","confidence":90,"sources":["source1"]}

            Надеюсь это помогло!
        """.trimIndent()

        val result = JsonExtractor.extract(input)
        assertEquals("""{"answer":"Ответ","confidence":90,"sources":["source1"]}""", result)
        assertTrue(JsonExtractor.isValidJson(result))
    }

    @Test
    fun `extract JSON array`() {
        val input = """[{"id":1},{"id":2}]"""
        val result = JsonExtractor.extract(input)
        assertEquals(input, result)
        assertTrue(JsonExtractor.isValidJson(result))
    }

    @Test
    fun `extract nested JSON`() {
        val input = """
            Смотри:
            {
              "data": {
                "nested": {
                  "value": "test"
                }
              }
            }
            Вот так!
        """.trimIndent()

        val result = JsonExtractor.extract(input)
        assertTrue(result.startsWith("{"))
        assertTrue(result.endsWith("}"))
        assertTrue(JsonExtractor.isValidJson(result))
    }

    @Test
    fun `extractAndValidate returns null for invalid JSON`() {
        val input = "This is not JSON at all"
        val result = JsonExtractor.extractAndValidate(input)
        assertEquals(null, result)
    }

    @Test
    fun `extractAndValidate returns JSON for valid input`() {
        val input = """{"valid":"json"}"""
        val result = JsonExtractor.extractAndValidate(input)
        assertEquals(input, result)
    }

    @Test
    fun `isValidJson detects valid JSON`() {
        assertTrue(JsonExtractor.isValidJson("""{"test":"value"}"""))
        assertTrue(JsonExtractor.isValidJson("""[1,2,3]"""))
        assertTrue(JsonExtractor.isValidJson("""{"nested":{"deep":"value"}}"""))
    }

    @Test
    fun `isValidJson detects invalid JSON`() {
        assertFalse(JsonExtractor.isValidJson("not json"))
        assertFalse(JsonExtractor.isValidJson("{invalid}"))
        assertFalse(JsonExtractor.isValidJson(""))
    }
}