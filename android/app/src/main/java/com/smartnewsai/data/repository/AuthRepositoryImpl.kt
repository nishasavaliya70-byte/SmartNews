package com.smartnewsai.data.repository

import com.smartnewsai.data.preferences.PreferenceManager
import com.smartnewsai.data.remote.api.AuthApiService
import com.smartnewsai.data.remote.dto.LoginRequestDto
import com.smartnewsai.data.remote.dto.RegisterRequestDto
import com.smartnewsai.domain.model.UserProfile
import com.smartnewsai.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApiService,
    private val preferenceManager: PreferenceManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val response = authApi.login(LoginRequestDto(email = email, password = password))
            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!.accessToken
                preferenceManager.saveAuthToken(token)
                Result.success(token)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, fullName: String, password: String): Result<String> {
        return try {
            val response = authApi.register(
                RegisterRequestDto(email = email, fullName = fullName, password = password)
            )
            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!.accessToken
                preferenceManager.saveAuthToken(token)
                Result.success(token)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUserProfile(): Result<UserProfile> {
        return try {
            val response = authApi.getProfile()
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(
                    UserProfile(
                        id = dto.id,
                        fullName = dto.fullName,
                        email = dto.email,
                        profileImageUrl = dto.profileImage,
                        selectedInterests = dto.selectedInterests ?: listOf("Technology", "Science")
                    )
                )
            } else {
                Result.failure(Exception("Unable to fetch user profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        preferenceManager.clearAuthToken()
        return Result.success(Unit)
    }
}
