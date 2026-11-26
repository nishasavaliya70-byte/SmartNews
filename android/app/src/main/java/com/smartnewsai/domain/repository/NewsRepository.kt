package com.smartnewsai.domain.repository

import com.smartnewsai.domain.model.NewsArticle

interface NewsRepository {
    suspend fun getNewsFeed(category: String?, page: Int, size: Int): Result<List<NewsArticle>>
    suspend fun getLatestNews(limit: Int): Result<List<NewsArticle>>
    suspend fun getTrendingNews(limit: Int): Result<List<NewsArticle>>
    suspend fun searchNews(query: String, category: String?, page: Int, size: Int): Result<List<NewsArticle>>
    suspend fun getArticleDetail(newsId: String): Result<NewsArticle>
}
