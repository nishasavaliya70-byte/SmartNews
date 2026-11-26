package com.smartnewsai.domain.repository

import com.smartnewsai.domain.model.NewsArticle

interface HistoryRepository {
    suspend fun getReadingHistory(limit: Int): Result<List<NewsArticle>>
    suspend fun recordHistory(newsId: String): Result<Unit>
    suspend fun clearHistory(): Result<Unit>
}
