package com.smartnewsai.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AiAnalysisRequestDto(
    @SerializedName("headline") val headline: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("article_url") val articleUrl: String? = null,
    @SerializedName("news_id") val newsId: String? = null
)

data class AiAnalysisResponseDto(
    @SerializedName("id") val id: String?,
    @SerializedName("news_id") val newsId: String?,
    @SerializedName("predicted_category") val predictedCategory: String,
    @SerializedName("classification_label") val classificationLabel: String,
    @SerializedName("confidence_score") val confidenceScore: Float,
    @SerializedName("credibility_score") val credibilityScore: Float,
    @SerializedName("model_version") val modelVersion: String,
    @SerializedName("explanation") val explanation: String?,
    @SerializedName("suspicious_language_signals") val suspiciousLanguageSignals: List<String>?,
    @SerializedName("key_takeaways") val keyTakeaways: List<String>?,
    @SerializedName("important_keywords") val importantKeywords: List<String>?,
    @SerializedName("verification_tips") val verificationTips: List<String>?
)
