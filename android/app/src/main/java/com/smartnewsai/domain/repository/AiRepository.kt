package com.smartnewsai.domain.repository

import com.smartnewsai.domain.model.AiAnalysisResult

interface AiRepository {
    suspend fun analyzeText(headline: String, content: String?, url: String?): Result<AiAnalysisResult>
}
