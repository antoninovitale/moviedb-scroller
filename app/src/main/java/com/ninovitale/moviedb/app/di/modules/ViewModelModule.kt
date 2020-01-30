package com.ninovitale.moviedb.app.di.modules

import androidx.lifecycle.ViewModelProvider
import com.ninovitale.moviedb.app.api.MovieApi
import com.ninovitale.moviedb.app.data.ConfigurationDao
import com.ninovitale.moviedb.app.data.MovieDataSourceFactory
import com.ninovitale.moviedb.app.data.MovieRepository
import com.ninovitale.moviedb.app.domain.MovieUseCase
import com.ninovitale.moviedb.app.ui.MovieBoundaryCallback
import com.ninovitale.moviedb.app.viewmodel.MovieViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ViewModelModule {
    @Provides
    @JvmStatic
    @Singleton
    fun provideUseCase(
        movieApi: MovieApi,
        movieRepository: MovieRepository,
        configDao: ConfigurationDao
    ) = MovieUseCase(movieApi, movieRepository, configDao)

    @Provides
    @JvmStatic
    @Singleton
    fun provideViewModelFactory(
        dataSourceFactory: MovieDataSourceFactory,
        boundaryCallback: MovieBoundaryCallback
    ): ViewModelProvider.Factory = MovieViewModelFactory(dataSourceFactory, boundaryCallback)
}