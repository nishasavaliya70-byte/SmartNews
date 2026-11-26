package com.smartnewsai.data.remote.api

import com.smartnewsai.data.remote.dto.TrendingCategoryDto
import com.smartnewsai.data.remote.dto.TrendingTopicDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TrendingApiService {

    @GET("trending/topics")
    suspend fun getTrendingTopics(@Query("time_window") timeWindow: String = "24h"): Response<List<TrendingTopicDto>>

    @GET("trending/categories")
    suspend fun getTrendingCategories(@Query("time_window") timeWindow: String = "24h"): Response<List<TrendingCategoryDto>>
}
