package com.smartnewsai.data.remote.api

import com.smartnewsai.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<TokenResponseDto>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<TokenResponseDto>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserDto>

    @GET("users/profile")
    suspend fun getProfile(): Response<UserDto>

    @PATCH("users/profile")
    suspend fun updateProfile(@Body update: UserUpdateDto): Response<UserDto>
}
