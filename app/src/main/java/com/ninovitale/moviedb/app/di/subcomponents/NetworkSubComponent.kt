package com.ninovitale.moviedb.app.di.subcomponents

import com.ninovitale.moviedb.app.api.MovieApi
import dagger.Subcomponent

@Subcomponent
interface NetworkSubComponent {
    val api: MovieApi

    @Subcomponent.Factory
    interface Factory {
        fun create(): NetworkSubComponent
    }
}