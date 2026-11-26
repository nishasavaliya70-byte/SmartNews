package com.smartnewsai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartnewsai.core.network.RetrofitClient
import com.smartnewsai.data.preferences.PreferenceManager
import com.smartnewsai.data.repository.AuthRepositoryImpl
import com.smartnewsai.data.repository.BookmarkRepositoryImpl
import com.smartnewsai.data.repository.HistoryRepositoryImpl
import com.smartnewsai.data.repository.NewsRepositoryImpl
import com.smartnewsai.presentation.screens.auth.AuthScreen
import com.smartnewsai.presentation.screens.auth.AuthViewModel
import com.smartnewsai.presentation.screens.main.MainScreen
import com.smartnewsai.presentation.screens.news_detail.NewsDetailScreen
import com.smartnewsai.presentation.screens.news_detail.NewsDetailViewModel
import com.smartnewsai.presentation.screens.onboarding.OnboardingScreen
import com.smartnewsai.presentation.screens.onboarding.OnboardingViewModel
import com.smartnewsai.presentation.screens.splash.SplashScreen
import com.smartnewsai.presentation.screens.splash.SplashViewModel
import com.smartnewsai.presentation.screens.summary.SummaryScreen
import com.smartnewsai.presentation.screens.summary.SummaryViewModel
import com.smartnewsai.presentation.screens.trending.TrendingScreen
import com.smartnewsai.presentation.screens.trending.TrendingViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    preferenceManager: PreferenceManager
) {
    val retrofitClient = remember { RetrofitClient(preferenceManager) }
    val authRepository = remember { AuthRepositoryImpl(retrofitClient.authApi, preferenceManager) }
    val newsRepository = remember { NewsRepositoryImpl(retrofitClient.newsApi) }
    val bookmarkRepository = remember { BookmarkRepositoryImpl(retrofitClient.bookmarkApi) }
    val historyRepository = remember { HistoryRepositoryImpl(retrofitClient.historyApi) }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Destination
        composable(Screen.Splash.route) {
            val splashViewModel = SplashViewModel(preferenceManager)
            SplashScreen(
                viewModel = splashViewModel,
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding Destination
        composable(Screen.Onboarding.route) {
            val onboardingViewModel = OnboardingViewModel(preferenceManager)
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onFinishOnboarding = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Authentication Screen (Login / Register)
        composable(Screen.Login.route) {
            val authViewModel = remember { AuthViewModel(authRepository) }
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Main Dashboard Screen (With 5-Tab Bottom Navigation)
        composable(Screen.Main.route) {
            MainScreen(
                preferenceManager = preferenceManager,
                onArticleClick = { newsId ->
                    navController.navigate(Screen.NewsDetail.createRoute(newsId))
                },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }

        // News Detail Screen with full AI Insights
        composable(
            route = Screen.NewsDetail.route,
            arguments = listOf(
                navArgument("newsId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId") ?: ""
            val detailViewModel = remember(newsId) {
                NewsDetailViewModel(newsRepository, bookmarkRepository, historyRepository)
            }
            NewsDetailScreen(
                newsId = newsId,
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // AI Summary Screen
        composable(Screen.Summary.route) {
            val summaryViewModel = remember { SummaryViewModel(retrofitClient.aiApi) }
            SummaryScreen(viewModel = summaryViewModel)
        }

        // Trending Intelligence Screen
        composable(Screen.Trending.route) {
            val trendingViewModel = remember { TrendingViewModel(retrofitClient.trendingApi) }
            TrendingScreen(viewModel = trendingViewModel)
        }
    }
}
