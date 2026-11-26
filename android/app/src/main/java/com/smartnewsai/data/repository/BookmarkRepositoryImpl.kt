package com.smartnewsai.data.repository

import com.smartnewsai.data.remote.api.BookmarkApiService
import com.smartnewsai.data.remote.dto.BookmarkRequestDto
import com.smartnewsai.domain.model.NewsArticle
import com.smartnewsai.domain.repository.BookmarkRepository

class BookmarkRepositoryImpl(
    private val bookmarkApi: BookmarkApiService
) : BookmarkRepository {

    override suspend fun getBookmarks(): Result<List<NewsArticle>> {
        return try {
            val response = bookmarkApi.getBookmarks()
            if (response.isSuccessful && response.body() != null) {
                val articles = response.body()!!.map { item ->
                    val dto = item.article
                    NewsArticle(
                        id = dto.id,
                        title = dto.title,
                        description = dto.description,
                        content = dto.content,
                        sourceName = dto.sourceName,
                        sourceUrl = dto.sourceUrl,
                        articleUrl = dto.articleUrl,
                        imageUrl = dto.imageUrl,
                        categoryName = dto.categoryName ?: "General",
                        publishedAt = dto.publishedAt.take(10),
                        author = dto.sourceName,
                        isBookmarked = true,
                        aiCredibilityScore = dto.aiCredibilityScore ?: 0.94f
                    )
                }
                Result.success(articles)
            } else {
                Result.failure(Exception("Failed to fetch bookmarks"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addBookmark(newsId: String): Result<Unit> {
        return try {
            val response = bookmarkApi.addBookmark(BookmarkRequestDto(newsId = newsId))
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Could not add bookmark"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeBookmark(newsId: String): Result<Unit> {
        return try {
            val response = bookmarkApi.removeBookmark(newsId = newsId)
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Could not remove bookmark"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
