package com.ninovitale.moviedb.app.domain

import com.ninovitale.moviedb.app.BuildConfig
import com.ninovitale.moviedb.app.api.MovieApi
import com.ninovitale.moviedb.app.api.model.Page
import com.ninovitale.moviedb.app.data.ConfigurationDao
import com.ninovitale.moviedb.app.data.ConfigurationDbItem
import com.ninovitale.moviedb.app.data.MovieDbItem
import com.ninovitale.moviedb.app.data.MovieRepository
import com.ninovitale.moviedb.app.tools.MySchedulers
import io.reactivex.observers.DisposableSingleObserver

class MovieUseCase(
    private val api: MovieApi,
    private val repo: MovieRepository,
    private val configDao: ConfigurationDao
) {
    fun loadMovies(requestPage: Int = 1, completion: ((CompletionEvent) -> Unit)? = null) {
        var configurationDbItem: ConfigurationDbItem? = null

        configDao.load()
            .filter { storedConfig ->
                configurationDbItem = storedConfig
                isConfigurationValid(storedConfig)
            }
            .isEmpty
            .flatMap { noConfigurationStored ->
                if (noConfigurationStored) {
                    api.getConfiguration(BuildConfig.API_KEY)
                        .flatMap { configuration ->
                            configurationDbItem = ConfigurationDbItem(
                                imageBaseUrl = configuration.images?.secureBaseUrl
                                    ?: throw IllegalArgumentException("Image baseUrl cannot be null!"),
                                imageSize = configuration.images.posterSizes?.get(0)
                            ).also { configDao.insert(it) }

                            api.getPopularMovies(BuildConfig.API_KEY, page = requestPage)
                        }
                } else api.getPopularMovies(BuildConfig.API_KEY, page = requestPage)
            }
            .subscribeOn(MySchedulers.ioThread)
            .observeOn(MySchedulers.ioThread)
            .doOnSubscribe { completion?.invoke(CompletionEvent.Loading) }
            .subscribe(object : DisposableSingleObserver<Page>() {
                override fun onSuccess(page: Page) {
                    val results = page.results?.map {
                        with(it) {
                            MovieDbItem(
                                id,
                                originalTitle,
                                overview,
                                getImagePath(
                                    configurationDbItem?.imageBaseUrl,
                                    configurationDbItem?.imageSize,
                                    posterPath
                                ),
                                releaseDate,
                                voteAverage
                            )
                        }
                    }
                    results?.let {
                        repo.write(it)
                        val nextPage = if (requestPage >= page.totalPages) null else requestPage + 1
                        completion?.invoke(CompletionEvent.Loaded(it, nextPage))
                    }
                    dispose()
                }

                override fun onError(e: Throwable) {
                    completion?.invoke(CompletionEvent.Error(e))
                    dispose()
                }
            })
    }

    private fun isConfigurationValid(it: ConfigurationDbItem) =
        System.currentTimeMillis() - it.timestamp < REFRESH_CONFIG

    private fun getImagePath(baseUrl: String?, size: String?, posterPath: String?): String? {
        posterPath ?: return null
        baseUrl ?: return null
        size ?: return null
        return "$baseUrl$size$posterPath"
    }

    companion object {
        private const val REFRESH_CONFIG = 5 * 24 * 60 * 60 * 1000L //5 days
    }
}

sealed class CompletionEvent {
    object Loading : CompletionEvent()
    data class Loaded(val items: List<MovieDbItem>, val nextPage: Int?) : CompletionEvent()
    data class Error(val throwable: Throwable) : CompletionEvent()
}