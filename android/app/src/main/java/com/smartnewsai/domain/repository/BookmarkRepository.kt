package com.smartnewsai.domain.repository

import com.smartnewsai.domain.model.NewsArticle

interface BookmarkRepository {
    suspend fun getBookmarks(): Result<List<NewsArticle>>
    suspend fun addBookmark(newsId: String): Result<Unit>
    suspend fun removeBookmark(newsId: String): Result<Unit>
}
