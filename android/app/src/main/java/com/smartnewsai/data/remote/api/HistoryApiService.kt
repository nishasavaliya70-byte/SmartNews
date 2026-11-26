package com.smartnewsai.data.remote.api

import com.smartnewsai.data.remote.dto.NewsArticleDto
import com.smartnewsai.data.remote.dto.RecordHistoryRequestDto
import retrofit2.Response
import retrofit2.http.*

interface HistoryApiService {

    @GET("history")
    suspend fun getHistory(@Query("limit") limit: Int = 20): Response<List<NewsArticleDto>>

    @POST("history")
    suspend fun recordHistory(@Body request: RecordHistoryRequestDto): Response<Map<String, String>>

    @DELETE("history")
    suspend fun clearHistory(): Response<Map<String, Any>>
}
