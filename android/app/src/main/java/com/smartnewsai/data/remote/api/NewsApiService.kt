package com.smartnewsai.data.remote.api

import com.smartnewsai.data.remote.dto.NewsArticleDto
import com.smartnewsai.data.remote.dto.NewsListResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApiService {

    @GET("news")
    suspend fun getNewsFeed(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Response<NewsListResponseDto>

    @GET("news/latest")
    suspend fun getLatestNews(
        @Query("limit") limit: Int = 10
    ): Response<NewsListResponseDto>

    @GET("news/trending")
    suspend fun getTrendingNews(
        @Query("limit") limit: Int = 5
    ): Response<NewsListResponseDto>

    @GET("news/search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Response<NewsListResponseDto>

    @GET("news/category/{category_slug}")
    suspend fun getNewsByCategory(
        @Path("category_slug") categorySlug: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Response<NewsListResponseDto>

    @GET("news/{news_id}")
    suspend fun getArticleDetail(
        @Path("news_id") newsId: String
    ): Response<NewsArticleDto>
}
