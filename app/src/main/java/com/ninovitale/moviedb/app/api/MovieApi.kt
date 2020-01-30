package com.ninovitale.moviedb.app.api

import com.ninovitale.moviedb.app.api.model.Configuration
import com.ninovitale.moviedb.app.api.model.Page
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MovieApi {
    @Headers("Accept: application/json")
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String? = null,
        @Query("region") region: String? = null,
        @Query("page") page: Int? = null
    ): Single<Page>

    @Headers("Accept: application/json")
    @GET("configuration")
    fun getConfiguration(@Query("api_key") apiKey: String): Single<Configuration>
}