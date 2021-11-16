package com.lacklab.app.githubtest.data.remote.api

import com.lacklab.app.githubtest.factory.DataCallAdapterFactory
import com.lacklab.app.githubtest.data.model.GitHubUsers
import com.lacklab.app.githubtest.data.remote.ApiResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {

    @GET("search/users")
    suspend fun searchUsers(
        @Query("q", encoded = true) query: String,
        @Query("order") order: String? = "desc",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ) : ApiResponse<GitHubUsers>

    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create() : GithubApi {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(DataCallAdapterFactory())
                .build()
                .create(GithubApi::class.java)
        }
    }
}