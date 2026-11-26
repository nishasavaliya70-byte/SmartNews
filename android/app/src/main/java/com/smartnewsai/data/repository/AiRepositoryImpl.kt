package com.smartnewsai.data.repository

import com.smartnewsai.data.remote.api.AiApiService
import com.smartnewsai.data.remote.dto.AiAnalysisRequestDto
import com.smartnewsai.domain.model.AiAnalysisResult
import com.smartnewsai.domain.repository.AiRepository

class AiRepositoryImpl(
    private val aiApi: AiApiService
) : AiRepository {

    override suspend fun analyzeText(headline: String, content: String?, url: String?): Result<AiAnalysisResult> {
        return try {
            val response = aiApi.analyzeArticle(
                AiAnalysisRequestDto(
                    headline = headline.ifEmpty { null },
                    content = content?.ifEmpty { null },
                    articleUrl = url?.ifEmpty { null }
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(
                    AiAnalysisResult(
                        headline = headline,
                        predictedCategory = dto.predictedCategory,
                        classificationConfidence = dto.confidenceScore,
                        credibilityScore = dto.credibilityScore,
                        credibilityLabel = dto.classificationLabel,
                        suspiciousLanguageSignals = dto.suspiciousLanguageSignals ?: emptyList(),
                        keyTakeaways = dto.keyTakeaways ?: emptyList(),
                        importantKeywords = dto.importantKeywords ?: emptyList(),
                        modelExplanation = dto.explanation ?: "Analysis completed by SmartNews ML pipeline.",
                        verificationTips = dto.verificationTips ?: emptyList(),
                        modelVersion = dto.modelVersion
                    )
                )
            } else {
                Result.failure(Exception("AI analysis request failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
