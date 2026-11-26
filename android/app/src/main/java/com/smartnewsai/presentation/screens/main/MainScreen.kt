package com.smartnewsai.presentation.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.smartnewsai.core.network.RetrofitClient
import com.smartnewsai.data.preferences.PreferenceManager
import com.smartnewsai.data.repository.AiRepositoryImpl
import com.smartnewsai.data.repository.AuthRepositoryImpl
import com.smartnewsai.data.repository.BookmarkRepositoryImpl
import com.smartnewsai.data.repository.NewsRepositoryImpl
import com.smartnewsai.presentation.components.AppBottomNavigation
import com.smartnewsai.presentation.components.NavigationTab
import com.smartnewsai.presentation.screens.ai_analyzer.AiAnalyzerScreen
import com.smartnewsai.presentation.screens.ai_analyzer.AiAnalyzerViewModel
import com.smartnewsai.presentation.screens.bookmarks.BookmarksScreen
import com.smartnewsai.presentation.screens.bookmarks.BookmarksViewModel
import com.smartnewsai.presentation.screens.explore.ExploreScreen
import com.smartnewsai.presentation.screens.explore.ExploreViewModel
import com.smartnewsai.presentation.screens.home.HomeScreen
import com.smartnewsai.presentation.screens.home.HomeViewModel
import com.smartnewsai.presentation.screens.profile.ProfileScreen
import com.smartnewsai.presentation.screens.profile.ProfileViewModel

@Composable
fun MainScreen(
    preferenceManager: PreferenceManager,
    onArticleClick: (String) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTabRoute by remember { mutableStateOf(NavigationTab.Home.route) }

    // Initialize Network Client & Repositories
    val retrofitClient = remember { RetrofitClient(preferenceManager) }
    val newsRepository = remember { NewsRepositoryImpl(retrofitClient.newsApi) }
    val bookmarkRepository = remember { BookmarkRepositoryImpl(retrofitClient.bookmarkApi) }
    val authRepository = remember { AuthRepositoryImpl(retrofitClient.authApi, preferenceManager) }
    val aiRepository = remember { AiRepositoryImpl(retrofitClient.aiApi) }

    // ViewModels with real repository & AI service injection
    val homeViewModel = remember { HomeViewModel(newsRepository, bookmarkRepository, retrofitClient.aiApi) }
    val exploreViewModel = remember { ExploreViewModel(newsRepository, bookmarkRepository) }
    val bookmarksViewModel = remember { BookmarksViewModel(bookmarkRepository) }
    val aiAnalyzerViewModel = remember { AiAnalyzerViewModel(aiRepository) }
    val profileViewModel = remember { ProfileViewModel(authRepository, preferenceManager) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            AppBottomNavigation(
                selectedRoute = currentTabRoute,
                onTabSelected = { tab ->
                    currentTabRoute = tab.route
                    if (tab == NavigationTab.Bookmarks) {
                        bookmarksViewModel.loadBookmarks()
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTabRoute) {
                NavigationTab.Home.route -> {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onArticleClick = onArticleClick,
                        onNavigateToAiAnalyzer = {
                            currentTabRoute = NavigationTab.AiAnalyze.route
                        }
                    )
                }
                NavigationTab.Explore.route -> {
                    ExploreScreen(
                        viewModel = exploreViewModel,
                        onArticleClick = onArticleClick
                    )
                }
                NavigationTab.AiAnalyze.route -> {
                    AiAnalyzerScreen(
                        viewModel = aiAnalyzerViewModel
                    )
                }
                NavigationTab.Bookmarks.route -> {
                    BookmarksScreen(
                        viewModel = bookmarksViewModel,
                        onArticleClick = onArticleClick
                    )
                }
                NavigationTab.Profile.route -> {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onLogoutClick = onLogoutClick
                    )
                }
            }
        }
    }
}
