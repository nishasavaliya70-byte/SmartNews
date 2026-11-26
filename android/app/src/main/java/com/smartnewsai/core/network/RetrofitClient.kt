package com.smartnewsai.core.network

import com.smartnewsai.data.preferences.PreferenceManager
import com.smartnewsai.data.remote.api.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient(private val preferenceManager: PreferenceManager) {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(preferenceManager))
        .addInterceptor(loggingInterceptor)
        .connectTimeout(Constants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(Constants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(Constants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApiService = retrofit.create(AuthApiService::class.java)
    val newsApi: NewsApiService = retrofit.create(NewsApiService::class.java)
    val bookmarkApi: BookmarkApiService = retrofit.create(BookmarkApiService::class.java)
    val historyApi: HistoryApiService = retrofit.create(HistoryApiService::class.java)
    val aiApi: AiApiService = retrofit.create(AiApiService::class.java)
    val trendingApi: TrendingApiService = retrofit.create(TrendingApiService::class.java)
}
