package com.smartnewsai.data.remote.api

import com.smartnewsai.data.remote.dto.BookmarkItemDto
import com.smartnewsai.data.remote.dto.BookmarkRequestDto
import retrofit2.Response
import retrofit2.http.*

interface BookmarkApiService {

    @GET("bookmarks")
    suspend fun getBookmarks(): Response<List<BookmarkItemDto>>

    @POST("bookmarks")
    suspend fun addBookmark(@Body request: BookmarkRequestDto): Response<Map<String, String>>

    @DELETE("bookmarks/{news_id}")
    suspend fun removeBookmark(@Path("news_id") newsId: String): Response<Map<String, String>>
}
