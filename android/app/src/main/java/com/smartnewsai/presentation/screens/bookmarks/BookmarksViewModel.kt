package com.smartnewsai.presentation.screens.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartnewsai.domain.model.NewsArticle
import com.smartnewsai.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BookmarksUiState(
    val query: String = "",
    val bookmarkedArticles: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class BookmarksViewModel(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()

    init {
        loadBookmarks()
    }

    fun loadBookmarks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = bookmarkRepository.getBookmarks()
            result.onSuccess { items ->
                _uiState.value = _uiState.value.copy(bookmarkedArticles = items, isLoading = false)
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
    }

    fun removeBookmark(articleId: String) {
        viewModelScope.launch {
            bookmarkRepository.removeBookmark(articleId)
            val filtered = _uiState.value.bookmarkedArticles.filter { it.id != articleId }
            _uiState.value = _uiState.value.copy(bookmarkedArticles = filtered)
        }
    }
}
