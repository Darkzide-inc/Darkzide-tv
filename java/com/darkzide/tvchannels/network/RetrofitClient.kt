package com.darkzide.tvchannels.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val GITHUB_BASE_URL = "https://api.github.com/"
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val gitHubService: GitHubService by lazy {
        retrofit.create(GitHubService::class.java)
    }
}