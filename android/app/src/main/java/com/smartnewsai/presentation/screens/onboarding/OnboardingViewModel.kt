package com.smartnewsai.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartnewsai.data.preferences.PreferenceManager
import com.smartnewsai.domain.model.OnboardingItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val pages = listOf(
        OnboardingItem(
            badgeText = "MULTI-SOURCE COLLECTION",
            title = "Global News from Verified Publishers",
            description = "Stay informed with real-time news curated across international, national, and domain-specific verified media sources."
        ),
        OnboardingItem(
            badgeText = "NLP & CREDIBILITY",
            title = "AI Summaries & Deep Fact Insights",
            description = "Understand complex stories in seconds with abstractive summaries, automated categorization, and probabilistic credibility signals."
        ),
        OnboardingItem(
            badgeText = "PERSONALIZED INTELLIGENCE",
            title = "Tailored to Your Curious Mind",
            description = "Experience an evolving feed powered by content-based filtering that adapts to your bookmarks, reading habits, and focus topics."
        )
    )

    private val _navigateToAuth = MutableSharedFlow<Unit>()
    val navigateToAuth: SharedFlow<Unit> = _navigateToAuth.asSharedFlow()

    fun completeOnboarding() {
        viewModelScope.launch {
            preferenceManager.setOnboardingCompleted(true)
            _navigateToAuth.emit(Unit)
        }
    }
}
