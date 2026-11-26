package com.smartnewsai.core.network

import com.smartnewsai.data.preferences.PreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val preferenceManager: PreferenceManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Read token from DataStore (synchronously within interceptor)
        val token = runBlocking {
            preferenceManager.authToken.first()
        }

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        requestBuilder.addHeader("Accept", "application/json")
        requestBuilder.addHeader("Content-Type", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}
