package com.smartnewsai.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object NewsDetail : Screen("news_detail/{newsId}") {
        fun createRoute(newsId: String) = "news_detail/$newsId"
    }
    object AiAnalyzer : Screen("ai_analyzer")
    object Summary : Screen("summary")
    object Trending : Screen("trending")
}
