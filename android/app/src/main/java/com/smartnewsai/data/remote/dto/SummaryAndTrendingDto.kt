package com.smartnewsai.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SummarizeRequestDto(
    @SerializedName("news_id") val newsId: String?,
    @SerializedName("text") val text: String?,
    @SerializedName("summary_type") val summaryType: String
)

data class SummarizeResponseDto(
    @SerializedName("news_id") val newsId: String?,
    @SerializedName("summary") val summary: String,
    @SerializedName("summary_type") val summaryType: String,
    @SerializedName("key_takeaways") val keyTakeaways: List<String>?,
    @SerializedName("important_entities") val importantEntities: List<String>?,
    @SerializedName("main_topic") val mainTopic: String?,
    @SerializedName("model_version") val modelVersion: String?
)

data class TrendingTopicDto(
    @SerializedName("topic_name") val topicName: String,
    @SerializedName("article_count") val articleCount: Int,
    @SerializedName("growth_rate") val growthRate: Float,
    @SerializedName("growth_label") val growthLabel: String,
    @SerializedName("time_window") val timeWindow: String
)

data class TrendingCategoryDto(
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("article_count") val articleCount: Int,
    @SerializedName("percentage_share") val percentageShare: Float
)
