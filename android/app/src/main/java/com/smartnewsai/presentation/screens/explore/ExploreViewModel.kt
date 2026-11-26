package com.smartnewsai.presentation.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartnewsai.domain.model.NewsArticle
import com.smartnewsai.domain.repository.BookmarkRepository
import com.smartnewsai.domain.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExploreUiState(
    val query: String = "",
    val selectedCategory: String = "all",
    val trendingKeywords: List<String> = listOf(
        "QuantumComputing", "FastAPI", "CleanEnergy", "Biotech", "Semiconductors", "DeepLearning"
    ),
    val articles: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ExploreViewModel(
    private val newsRepository: NewsRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadExploreArticles()
    }

    fun loadExploreArticles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val current = _uiState.value

            val result = if (current.query.isNotEmpty()) {
                newsRepository.searchNews(
                    query = current.query,
                    category = current.selectedCategory,
                    page = 1,
                    size = 20
                )
            } else {
                newsRepository.getNewsFeed(
                    category = current.selectedCategory,
                    page = 1,
                    size = 20
                )
            }

            result.onSuccess { items ->
                _uiState.value = _uiState.value.copy(articles = items, isLoading = false)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage
                )
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        loadExploreArticles()
    }

    fun onKeywordClick(keyword: String) {
        _uiState.value = _uiState.value.copy(query = keyword)
        loadExploreArticles()
    }

    fun onCategorySelect(categoryId: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = categoryId)
        loadExploreArticles()
    }

    fun toggleBookmark(articleId: String) {
        val target = _uiState.value.articles.find { it.id == articleId } ?: return
        viewModelScope.launch {
            if (target.isBookmarked) {
                bookmarkRepository.removeBookmark(articleId)
            } else {
                bookmarkRepository.addBookmark(articleId)
            }

            val updated = _uiState.value.articles.map {
                if (it.id == articleId) it.copy(isBookmarked = !it.isBookmarked) else it
            }
            _uiState.value = _uiState.value.copy(articles = updated)
        }
    }
}
