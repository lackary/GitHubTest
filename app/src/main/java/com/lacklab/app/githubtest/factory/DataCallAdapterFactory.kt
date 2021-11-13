package com.lacklab.app.githubtest.factory

import com.lacklab.app.githubtest.data.remote.ApiResponse
import com.lacklab.app.githubtest.data.remote.DataCallAdapter
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class DataCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Call::class.java != getRawType(returnType)) return null
        check(returnType is ParameterizedType) {
            "Return type must be a parameterized type."
        }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != ApiResponse::class.java) return null
        check(responseType is ParameterizedType) {
            "Response type must be a parameterized type."
        }

        val successType = getParameterUpperBound(0, responseType)

        return DataCallAdapter<Any>(successType)

    }
}