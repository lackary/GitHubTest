package com.lacklab.app.githubtest.data.remote

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class DataCall<T>constructor(
    private val call: Call<T>
) : Call<ApiResponse<T>> {
    override fun enqueue(callback: Callback<ApiResponse<T>>) =
        call.enqueue(object: Callback<T>{
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val apiResponse = ApiResponse.create(response)
                callback.onResponse(this@DataCall, Response.success(apiResponse))
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                Timber.d("onFailure: message ${throwable.message}")
                val apiResponse = ApiResponse.create<T>(throwable)
                callback.onResponse(this@DataCall, Response.success(apiResponse))
            }

        })

    override fun clone(): Call<ApiResponse<T>> {
        TODO("Not yet implemented")
    }

    override fun execute(): Response<ApiResponse<T>> {
        TODO("Not yet implemented")
    }

    override fun isExecuted(): Boolean {
        TODO("Not yet implemented")
    }

    override fun cancel() {
        call.cancel()
    }

    override fun isCanceled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun request(): Request {
        TODO("Not yet implemented")
    }

    override fun timeout(): Timeout {
        TODO("Not yet implemented")
    }
}