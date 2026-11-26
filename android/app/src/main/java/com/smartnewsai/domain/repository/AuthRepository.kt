package com.smartnewsai.domain.repository

import com.smartnewsai.domain.model.UserProfile

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<String>
    suspend fun register(email: String, fullName: String, password: String): Result<String>
    suspend fun getCurrentUserProfile(): Result<UserProfile>
    suspend fun logout(): Result<Unit>
}
