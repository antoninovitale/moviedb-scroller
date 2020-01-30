package com.ninovitale.moviedb.app.api.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Images(
    @Json(name = "poster_sizes")
    val posterSizes: List<String>? = null,
    @Json(name = "secure_base_url")
    val secureBaseUrl: String? = null
)