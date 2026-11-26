package com.smartnewsai.domain.model

data class NewsArticle(
    val id: String,
    val title: String,
    val description: String,
    val content: String? = null,
    val sourceName: String,
    val sourceUrl: String? = null,
    val articleUrl: String,
    val imageUrl: String? = null,
    val categoryName: String = "General",
    val publishedAt: String,
    val author: String? = null,
    val isBookmarked: Boolean = false,
    val aiCredibilityScore: Float? = null, // e.g. 0.88 (88% probabilistic credibility)
    val aiCategoryPrediction: String? = null,
    val aiSummaryShort: String? = null
)
