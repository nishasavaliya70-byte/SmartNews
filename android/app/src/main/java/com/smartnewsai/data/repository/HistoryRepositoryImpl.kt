package com.smartnewsai.data.repository

import com.smartnewsai.data.remote.api.HistoryApiService
import com.smartnewsai.data.remote.dto.RecordHistoryRequestDto
import com.smartnewsai.domain.model.NewsArticle
import com.smartnewsai.domain.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val historyApi: HistoryApiService
) : HistoryRepository {

    override suspend fun getReadingHistory(limit: Int): Result<List<NewsArticle>> {
        return try {
            val response = historyApi.getHistory(limit = limit)
            if (response.isSuccessful && response.body() != null) {
                val articles = response.body()!!.map { dto ->
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
                        isBookmarked = dto.isBookmarked ?: false
                    )
                }
                Result.success(articles)
            } else {
                Result.failure(Exception("Failed to load history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun recordHistory(newsId: String): Result<Unit> {
        return try {
            val response = historyApi.recordHistory(RecordHistoryRequestDto(newsId = newsId))
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Failed to record history"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearHistory(): Result<Unit> {
        return try {
            val response = historyApi.clearHistory()
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Failed to clear history"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
