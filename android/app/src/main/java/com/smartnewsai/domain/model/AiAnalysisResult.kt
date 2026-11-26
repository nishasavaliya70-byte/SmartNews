package com.smartnewsai.domain.model

data class AiAnalysisResult(
    val headline: String,
    val predictedCategory: String,
    val classificationConfidence: Float, // 0.0 to 1.0
    val credibilityScore: Float,         // 0.0 to 1.0
    val credibilityLabel: String,        // e.g. "High Credibility", "Medium Credibility", "Suspicious Signals Detected"
    val suspiciousLanguageSignals: List<String> = emptyList(),
    val keyTakeaways: List<String> = emptyList(),
    val importantKeywords: List<String> = emptyList(),
    val modelExplanation: String,
    val verificationTips: List<String> = emptyList(),
    val modelVersion: String = "v1.2-DistilBERT-TFIDF"
)
