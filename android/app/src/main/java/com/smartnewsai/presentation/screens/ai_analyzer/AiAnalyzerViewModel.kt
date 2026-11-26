package com.smartnewsai.presentation.screens.ai_analyzer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartnewsai.domain.model.AiAnalysisResult
import com.smartnewsai.domain.repository.AiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AiAnalyzerUiState(
    val headlineInput: String = "",
    val articleTextInput: String = "",
    val urlInput: String = "",
    val isAnalyzing: Boolean = false,
    val result: AiAnalysisResult? = null,
    val errorMessage: String? = null
)

class AiAnalyzerViewModel(
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiAnalyzerUiState())
    val uiState: StateFlow<AiAnalyzerUiState> = _uiState.asStateFlow()

    fun onHeadlineChanged(text: String) {
        _uiState.value = _uiState.value.copy(headlineInput = text, errorMessage = null)
    }

    fun onArticleTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(articleTextInput = text, errorMessage = null)
    }

    fun onUrlChanged(text: String) {
        _uiState.value = _uiState.value.copy(urlInput = text, errorMessage = null)
    }

    fun runAnalysis() {
        val current = _uiState.value
        val headline = current.headlineInput.trim()
        val content = current.articleTextInput.trim()
        val url = current.urlInput.trim()

        if (headline.isEmpty() && content.isEmpty() && url.isEmpty()) {
            _uiState.value = current.copy(errorMessage = "Please enter a headline, article text, or URL to analyze.")
            return
        }

        _uiState.value = current.copy(isAnalyzing = true, errorMessage = null)

        viewModelScope.launch {
            val analysisResult = aiRepository.analyzeText(
                headline = headline,
                content = content,
                url = url
            )

            analysisResult.onSuccess { data ->
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    result = data
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    errorMessage = error.localizedMessage ?: "Failed to perform AI analysis"
                )
            }
        }
    }

    fun reset() {
        _uiState.value = AiAnalyzerUiState()
    }
}
