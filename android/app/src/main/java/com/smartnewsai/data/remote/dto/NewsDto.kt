package com.smartnewsai.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NewsArticleDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("content") val content: String?,
    @SerializedName("source_name") val sourceName: String,
    @SerializedName("source_url") val sourceUrl: String?,
    @SerializedName("article_url") val articleUrl: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("category_id") val categoryId: String?,
    @SerializedName("category_name") val categoryName: String?,
    @SerializedName("published_at") val publishedAt: String,
    @SerializedName("is_bookmarked") val isBookmarked: Boolean?,
    @SerializedName("ai_credibility_score") val aiCredibilityScore: Float?,
    @SerializedName("ai_category_prediction") val aiCategoryPrediction: String?,
    @SerializedName("ai_summary_short") val aiSummaryShort: String?
)

data class NewsListResponseDto(
    @SerializedName("items") val items: List<NewsArticleDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int
)

data class BookmarkRequestDto(
    @SerializedName("news_id") val newsId: String
)

data class BookmarkItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("news_id") val newsId: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("article") val article: NewsArticleDto
)

data class RecordHistoryRequestDto(
    @SerializedName("news_id") val newsId: String
)
