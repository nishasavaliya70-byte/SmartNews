package com.smartnewsai.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("password") val password: String
)

data class TokenResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("profile_image") val profileImage: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("selected_interests") val selectedInterests: List<String>?
)

data class UserUpdateDto(
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("profile_image") val profileImage: String?
)
