package com.smartnewsai.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartnewsai.data.remote.api.AiApiService
import com.smartnewsai.domain.model.Category
import com.smartnewsai.domain.model.NewsArticle
import com.smartnewsai.domain.repository.BookmarkRepository
import com.smartnewsai.domain.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val userName: String = "Reader",
    val greeting: String = "Good Morning",
    val searchQuery: String = "",
    val selectedCategoryId: String = "all",
    val categories: List<Category> = Category.predefinedCategories,
    val featuredArticle: NewsArticle? = null,
    val forYouArticles: List<NewsArticle> = emptyList(),
    val latestArticles: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val newsRepository: NewsRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val aiApi: AiApiService? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        determineGreeting()
        loadFeed()
    }

    private fun determineGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 4..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
        _uiState.value = _uiState.value.copy(greeting = greeting)
    }

    fun loadFeed() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val selectedCat = _uiState.value.selectedCategoryId
            val feedResult = newsRepository.getNewsFeed(category = selectedCat, page = 1, size = 15)

            feedResult.onSuccess { articles ->
                val featured = articles.firstOrNull()
                var forYou = articles.drop(1).take(2)
                val latest = articles.drop(3)

                // Try fetching advanced AI personalized recommendations if available
                if (aiApi != null) {
                    try {
                        val recResponse = aiApi.getRecommendations(limit = 3)
                        if (recResponse.isSuccessful && !recResponse.body().isNullOrEmpty()) {
                            forYou = recResponse.body()!!.map { dto ->
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
                                    aiCredibilityScore = dto.aiCredibilityScore ?: 0.94f
                                )
                            }
                        }
                    } catch (_: Exception) {}
                }

                _uiState.value = _uiState.value.copy(
                    featuredArticle = featured,
                    forYouArticles = forYou,
                    latestArticles = latest,
                    isLoading = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Unable to fetch news feed"
                )
            }
        }
    }

    fun onCategorySelected(category: Category) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = category.id)
        loadFeed()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun toggleBookmark(articleId: String) {
        val current = _uiState.value
        val allArticles = listOfNotNull(current.featuredArticle) + current.forYouArticles + current.latestArticles
        val target = allArticles.find { it.id == articleId } ?: return

        viewModelScope.launch {
            if (target.isBookmarked) {
                bookmarkRepository.removeBookmark(articleId)
            } else {
                bookmarkRepository.addBookmark(articleId)
            }

            val updatedFeatured = current.featuredArticle?.let {
                if (it.id == articleId) it.copy(isBookmarked = !it.isBookmarked) else it
            }
            val updatedForYou = current.forYouArticles.map {
                if (it.id == articleId) it.copy(isBookmarked = !it.isBookmarked) else it
            }
            val updatedLatest = current.latestArticles.map {
                if (it.id == articleId) it.copy(isBookmarked = !it.isBookmarked) else it
            }

            _uiState.value = current.copy(
                featuredArticle = updatedFeatured,
                forYouArticles = updatedForYou,
                latestArticles = updatedLatest
            )
        }
    }
}
