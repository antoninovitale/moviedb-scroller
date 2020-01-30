package com.ninovitale.moviedb.app.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.ninovitale.moviedb.app.MovieApp
import com.ninovitale.moviedb.app.data.MovieRepository
import com.ninovitale.moviedb.app.di.modules.NetworkModule
import com.ninovitale.moviedb.app.di.modules.RepositoryModule
import com.ninovitale.moviedb.app.di.modules.ToolsModule
import com.ninovitale.moviedb.app.di.modules.ViewModelModule
import com.ninovitale.moviedb.app.di.subcomponents.NetworkSubComponent
import com.ninovitale.moviedb.app.tools.MyImageLoader
import com.ninovitale.moviedb.app.ui.MainActivity
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, ToolsModule::class, ViewModelModule::class, RepositoryModule::class])
interface ApplicationComponent : AndroidInjector<MovieApp> {
    val viewModelFactory: ViewModelProvider.Factory

    val repository: MovieRepository

    val imageLoader: MyImageLoader

    val networkSubComponentFactory: NetworkSubComponent.Factory

    fun inject(mainActivity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }
}