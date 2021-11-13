package com.lacklab.app.githubtest.data.remote

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class DataCallAdapter<R>(private val responseType: Type) :
    CallAdapter<R, Call<ApiResponse<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): Call<ApiResponse<R>> = DataCall(call)
}