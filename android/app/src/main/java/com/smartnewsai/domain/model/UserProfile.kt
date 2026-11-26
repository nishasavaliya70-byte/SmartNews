package com.smartnewsai.domain.model

data class UserProfile(
    val id: String,
    val fullName: String,
    val email: String,
    val profileImageUrl: String? = null,
    val selectedInterests: List<String> = emptyList(),
    val articlesReadCount: Int = 0,
    val bookmarksCount: Int = 0,
    val notificationsEnabled: Boolean = true,
    val isDarkMode: Boolean = false
)
