package com.smartstyle.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class GeminiResponseParserTest {

    private lateinit var service: GeminiService

    @Before
    fun setup() {
        service = GeminiService(apiKey = "test-key")
    }

    private val validJson = """
        {
          "assessment": "A relaxed, well-coordinated casual outfit.",
          "suggestions": ["Try a slimmer fit", "Add a statement accessory", "Tuck in the shirt"],
          "tags": ["casual", "blue", "summer", "weekend", "denim"]
        }
    """.trimIndent()

    @Test
    fun parseResponse_validJson_returnsCorrectAssessment() {
        val result = service.parseResponse(validJson)
        assertEquals("A relaxed, well-coordinated casual outfit.", result.assessment)
    }

    @Test
    fun parseResponse_validJson_returnsCorrectSuggestions() {
        val result = service.parseResponse(validJson)
        assertEquals(
            listOf("Try a slimmer fit", "Add a statement accessory", "Tuck in the shirt"),
            result.suggestions
        )
    }

    @Test
    fun parseResponse_validJson_returnsCorrectTags() {
        val result = service.parseResponse(validJson)
        assertEquals(listOf("casual", "blue", "summer", "weekend", "denim"), result.tags)
    }

    @Test
    fun parseResponse_stripsMarkdownCodeFences() {
        val fenced = "```json\n$validJson\n```"
        val result = service.parseResponse(fenced)
        assertEquals("A relaxed, well-coordinated casual outfit.", result.assessment)
    }

    @Test
    fun parseResponse_stripsPlainCodeFences() {
        val fenced = "```\n$validJson\n```"
        val result = service.parseResponse(fenced)
        assertEquals("A relaxed, well-coordinated casual outfit.", result.assessment)
    }

    @Test
    fun parseResponse_handlesLeadingAndTrailingWhitespace() {
        val result = service.parseResponse("   \n$validJson\n   ")
        assertEquals("A relaxed, well-coordinated casual outfit.", result.assessment)
    }

    @Test
    fun parseResponse_emptySuggestionsArray() {
        val json = """{"assessment":"Nice.","suggestions":[],"tags":["casual"]}"""
        val result = service.parseResponse(json)
        assertEquals(emptyList<String>(), result.suggestions)
    }

    @Test
    fun parseResponse_emptyTagsArray() {
        val json = """{"assessment":"Nice.","suggestions":["Tip"],"tags":[]}"""
        val result = service.parseResponse(json)
        assertEquals(emptyList<String>(), result.tags)
    }

    @Test
    fun parseResponse_invalidJson_throws() {
        assertThrows(Exception::class.java) {
            service.parseResponse("not json at all")
        }
    }

    @Test
    fun parseResponse_missingAssessmentField_throws() {
        val json = """{"suggestions":["tip"],"tags":["casual"]}"""
        assertThrows(Exception::class.java) {
            service.parseResponse(json)
        }
    }
}
