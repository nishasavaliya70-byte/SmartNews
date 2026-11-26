package com.smartnewsai.data.repository

import com.smartnewsai.data.remote.api.NewsApiService
import com.smartnewsai.data.remote.dto.NewsArticleDto
import com.smartnewsai.domain.model.NewsArticle
import com.smartnewsai.domain.repository.NewsRepository

class NewsRepositoryImpl(
    private val newsApi: NewsApiService
) : NewsRepository {

    override suspend fun getNewsFeed(category: String?, page: Int, size: Int): Result<List<NewsArticle>> {
        return try {
            val response = newsApi.getNewsFeed(
                category = if (category == "all") null else category,
                page = page,
                size = size
            )
            if (response.isSuccessful && response.body() != null) {
                val articles = response.body()!!.items.map { it.toDomain() }
                Result.success(articles)
            } else {
                Result.failure(Exception("Failed to fetch news feed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLatestNews(limit: Int): Result<List<NewsArticle>> {
        return try {
            val response = newsApi.getLatestNews(limit = limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.items.map { it.toDomain() })
            } else {
                Result.failure(Exception("Failed to fetch latest news"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrendingNews(limit: Int): Result<List<NewsArticle>> {
        return try {
            val response = newsApi.getTrendingNews(limit = limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.items.map { it.toDomain() })
            } else {
                Result.failure(Exception("Failed to fetch trending news"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchNews(query: String, category: String?, page: Int, size: Int): Result<List<NewsArticle>> {
        return try {
            val response = newsApi.searchNews(
                query = query,
                category = if (category == "all") null else category,
                page = page,
                size = size
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.items.map { it.toDomain() })
            } else {
                Result.failure(Exception("Failed to search news"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getArticleDetail(newsId: String): Result<NewsArticle> {
        return try {
            val response = newsApi.getArticleDetail(newsId = newsId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Article not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun NewsArticleDto.toDomain(): NewsArticle {
        return NewsArticle(
            id = id,
            title = title,
            description = description,
            content = content,
            sourceName = sourceName,
            sourceUrl = sourceUrl,
            articleUrl = articleUrl,
            imageUrl = imageUrl,
            categoryName = categoryName ?: "General",
            publishedAt = publishedAt.take(10),
            author = sourceName,
            isBookmarked = isBookmarked ?: false,
            aiCredibilityScore = aiCredibilityScore ?: 0.92f,
            aiCategoryPrediction = aiCategoryPrediction,
            aiSummaryShort = aiSummaryShort
        )
    }
}
