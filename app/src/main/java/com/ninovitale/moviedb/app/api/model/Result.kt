package com.ninovitale.moviedb.app.api.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Result(
    val id: Int,
    @Json(name = "original_title")
    val originalTitle: String? = null,
    val overview: String? = null,
    @Json(name = "poster_path")
    val posterPath: String? = null,
    @Json(name = "release_date")
    val releaseDate: String? = null,
    @Json(name = "vote_average")
    val voteAverage: Double? = null
)