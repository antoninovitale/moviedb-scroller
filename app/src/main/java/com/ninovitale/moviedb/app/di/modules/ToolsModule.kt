package com.ninovitale.moviedb.app.di.modules

import com.ninovitale.moviedb.app.tools.MyImageLoader
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ToolsModule {
    @Provides
    @Singleton
    @JvmStatic
    fun provideImageLoader(): MyImageLoader = MyImageLoader()
}