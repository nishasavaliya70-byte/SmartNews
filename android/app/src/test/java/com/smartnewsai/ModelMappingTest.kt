package com.smartnewsai

import com.smartnewsai.data.remote.dto.NewsArticleDto
import com.smartnewsai.domain.model.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelMappingTest {

    @Test
    fun testCategoryPredefinedList() {
        val categories = Category.predefinedCategories
        assertEquals(9, categories.size)
        assertTrue(categories.any { it.name == "Technology" })
        assertTrue(categories.any { it.name == "Business" })
        assertTrue(categories.any { it.name == "Science" })
    }

    @Test
    fun testNewsDtoFields() {
        val dto = NewsArticleDto(
            id = "test-123",
            title = "Breakthrough in Fusion Energy",
            description = "High beta plasma sustained for 120s",
            content = "Full editorial context...",
            sourceName = "Science Daily",
            sourceUrl = "https://example.com",
            articleUrl = "https://example.com/fusion-123",
            imageUrl = "https://example.com/image.jpg",
            language = "en",
            country = "us",
            categoryId = "cat-sci",
            categoryName = "Science",
            publishedAt = "2026-11-26T14:00:00Z",
            isBookmarked = true,
            aiCredibilityScore = 0.96f,
            aiCategoryPrediction = "Science",
            aiSummaryShort = "Summary text"
        )

        assertEquals("test-123", dto.id)
        assertEquals("Science Daily", dto.sourceName)
        assertEquals(0.96f, dto.aiCredibilityScore ?: 0f, 0.001f)
        assertNotNull(dto.publishedAt)
    }
}
