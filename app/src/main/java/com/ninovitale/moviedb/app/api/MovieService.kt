package com.ninovitale.moviedb.app.api

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class MovieService(moshi: Moshi, okHttpClient: OkHttpClient) {
    private val apiRetrofit: Retrofit

    init {
        val builder = Retrofit.Builder().client(okHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
        apiRetrofit = builder.build()
    }

    fun createApi(): MovieApi {
        return apiRetrofit.create(MovieApi::class.java)
    }

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"
    }
}