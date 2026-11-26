package com.smartnewsai.presentation.screens.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartnewsai.data.remote.api.AiApiService
import com.smartnewsai.data.remote.dto.SummarizeRequestDto
import com.smartnewsai.data.remote.dto.SummarizeResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SummaryUiState(
    val inputText: String = "",
    val selectedLength: String = "short", // "short", "detailed", "key_takeaways"
    val summaryResult: SummarizeResponseDto? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SummaryViewModel(
    private val aiApi: AiApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    fun onTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text, errorMessage = null)
    }

    fun onLengthSelected(length: String) {
        _uiState.value = _uiState.value.copy(selectedLength = length)
        if (_uiState.value.inputText.isNotEmpty()) {
            generateSummary()
        }
    }

    fun generateSummary() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter article text to summarize.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val response = aiApi.summarizeArticle(
                    SummarizeRequestDto(
                        newsId = null,
                        text = text,
                        summaryType = _uiState.value.selectedLength
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        summaryResult = response.body(),
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Summarization service failed."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Network error."
                )
            }
        }
    }
}
