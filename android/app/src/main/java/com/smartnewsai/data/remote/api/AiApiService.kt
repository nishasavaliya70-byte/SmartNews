package com.smartnewsai.data.remote.api

import com.smartnewsai.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AiApiService {

    @POST("ai/analyze")
    suspend fun analyzeArticle(@Body request: AiAnalysisRequestDto): Response<AiAnalysisResponseDto>

    @POST("ai/classify")
    suspend fun classifyCategory(@Body request: AiAnalysisRequestDto): Response<Map<String, Any>>

    @POST("ai/fake-news-detection")
    suspend fun detectFakeNews(@Body request: AiAnalysisRequestDto): Response<Map<String, Any>>

    @POST("ai/summarize")
    suspend fun summarizeArticle(@Body request: SummarizeRequestDto): Response<SummarizeResponseDto>

    @GET("ai/recommendations")
    suspend fun getRecommendations(@Query("limit") limit: Int = 10): Response<List<NewsArticleDto>>
}
