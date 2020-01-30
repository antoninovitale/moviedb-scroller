package com.ninovitale.moviedb.app.api.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Configuration(
    val images: Images? = null
)