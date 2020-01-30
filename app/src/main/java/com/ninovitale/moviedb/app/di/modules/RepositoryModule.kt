package com.ninovitale.moviedb.app.di.modules

import android.app.Application
import androidx.room.Room
import com.ninovitale.moviedb.app.data.MovieDao
import com.ninovitale.moviedb.app.data.MovieDatabase
import com.ninovitale.moviedb.app.data.MovieRepository
import com.ninovitale.moviedb.app.data.MovieRepositoryDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepositoryModule {
    @Provides
    @Singleton
    @JvmStatic
    fun provideDatabase(application: Application): MovieDatabase {
        return synchronized(MovieDatabase::class.java) {
            Room.databaseBuilder(
                application.applicationContext,
                MovieDatabase::class.java,
                "movies.db"
            ).build()
        }
    }

    @Provides
    @Singleton
    @JvmStatic
    fun provideDAO(dropCacheDatabase: MovieDatabase) = dropCacheDatabase.movieDAO()

    @Provides
    @Singleton
    @JvmStatic
    fun provideConfigDAO(dropCacheDatabase: MovieDatabase) = dropCacheDatabase.configDAO()

    @Provides
    @JvmStatic
    @Singleton
    fun provideRepository(dao: MovieDao): MovieRepository = MovieRepositoryDatabase(dao)
}