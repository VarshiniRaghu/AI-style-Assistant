package com.smartstyle.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class OutfitAnalysisEntityTest {

    private fun entity(
        id: Long = 1L,
        imageUri: String = "/data/analyses/123.jpg",
        assessment: String = "Great casual look.",
        suggestions: String = "Add a belt\nTry lighter shoes",
        tags: String = "casual,blue,summer",
        mlKitLabels: String = "Jeans,Shirt",
        timestamp: Long = 1000L
    ) = OutfitAnalysisEntity(id, imageUri, assessment, suggestions, tags, mlKitLabels, timestamp)

    @Test
    fun toDomain_mapsAllFieldsCorrectly() {
        val domain = entity().toDomain()

        assertEquals(1L, domain.id)
        assertEquals("/data/analyses/123.jpg", domain.imageUri)
        assertEquals("Great casual look.", domain.assessment)
        assertEquals(listOf("Add a belt", "Try lighter shoes"), domain.suggestions)
        assertEquals(listOf("casual", "blue", "summer"), domain.tags)
        assertEquals(listOf("Jeans", "Shirt"), domain.mlKitLabels)
        assertEquals(1000L, domain.timestamp)
    }

    @Test
    fun toDomain_filtersBlankSuggestions() {
        val domain = entity(suggestions = "Tip one\n\n\nTip two\n").toDomain()
        assertEquals(listOf("Tip one", "Tip two"), domain.suggestions)
    }

    @Test
    fun toDomain_filtersBlankTags() {
        val domain = entity(tags = "casual,,summer,").toDomain()
        assertEquals(listOf("casual", "summer"), domain.tags)
    }

    @Test
    fun toDomain_emptyStringsProduceEmptyLists() {
        val domain = entity(suggestions = "", tags = "", mlKitLabels = "").toDomain()
        assertEquals(emptyList<String>(), domain.suggestions)
        assertEquals(emptyList<String>(), domain.tags)
        assertEquals(emptyList<String>(), domain.mlKitLabels)
    }

    @Test
    fun toDomain_singleSuggestionAndTag() {
        val domain = entity(suggestions = "Only tip", tags = "formal").toDomain()
        assertEquals(listOf("Only tip"), domain.suggestions)
        assertEquals(listOf("formal"), domain.tags)
    }
}
