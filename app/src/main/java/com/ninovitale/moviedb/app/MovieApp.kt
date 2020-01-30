package com.ninovitale.moviedb.app

import android.app.Application
import com.ninovitale.moviedb.app.di.ApplicationComponent
import com.ninovitale.moviedb.app.di.DaggerApplicationComponent

class MovieApp : Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }
}