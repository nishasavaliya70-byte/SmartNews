package com.smartnewsai.presentation.screens.trending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartnewsai.data.remote.api.TrendingApiService
import com.smartnewsai.data.remote.dto.TrendingCategoryDto
import com.smartnewsai.data.remote.dto.TrendingTopicDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TrendingUiState(
    val timeWindow: String = "24h",
    val trendingTopics: List<TrendingTopicDto> = emptyList(),
    val trendingCategories: List<TrendingCategoryDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TrendingViewModel(
    private val trendingApi: TrendingApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrendingUiState())
    val uiState: StateFlow<TrendingUiState> = _uiState.asStateFlow()

    init {
        loadTrendingData()
    }

    fun loadTrendingData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val topicsRes = trendingApi.getTrendingTopics(timeWindow = _uiState.value.timeWindow)
                val catRes = trendingApi.getTrendingCategories(timeWindow = _uiState.value.timeWindow)

                val topics = if (topicsRes.isSuccessful) topicsRes.body() ?: emptyList() else emptyList()
                val categories = if (catRes.isSuccessful) catRes.body() ?: emptyList() else emptyList()

                _uiState.value = _uiState.value.copy(
                    trendingTopics = topics,
                    trendingCategories = categories,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage
                )
            }
        }
    }
}
