package com.ninovitale.moviedb.app.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ninovitale.moviedb.app.data.MovieDataSourceFactory
import com.ninovitale.moviedb.app.data.MovieDbItem
import com.ninovitale.moviedb.app.ui.MovieBoundaryCallback
import com.ninovitale.moviedb.app.ui.model.Movie
import com.ninovitale.moviedb.app.ui.model.ViewState
import javax.inject.Inject

class MovieViewModel(
    dataSourceFactory: MovieDataSourceFactory,
    boundaryCallback: MovieBoundaryCallback
) : ViewModel() {
    private val viewState: LiveData<ViewState>
    private val movies: LiveData<PagedList<Movie>>

    init {
        viewState =
            Transformations.switchMap(dataSourceFactory.getLiveData()) { dataSource -> dataSource.getViewState() }
                .also {
                    boundaryCallback.setLiveData(it as MutableLiveData<ViewState>)
                }

        movies =
            LivePagedListBuilder(
                dataSourceFactory.map { dbItem -> dbItem.mapDbToUiModel() },
                MovieDataSourceFactory.pagedListConfig()
            )
                .setBoundaryCallback(boundaryCallback)
                .build()
    }

    private fun MovieDbItem.mapDbToUiModel(): Movie {
        return Movie(
            id,
            originalTitle,
            overview,
            posterPath,
            transformDate(releaseDate),
            transformToPercentage(voteAverage)
        )
    }

    fun getViewState(): LiveData<ViewState> = viewState

    fun getMovies(): LiveData<PagedList<Movie>> = movies

    private fun transformToPercentage(value: Double?): String? =
        value?.let { "${(it * 10).toInt()}%" }

    private fun transformDate(releaseDate: String?) = releaseDate?.split("-")?.getOrNull(0)
}

class MovieViewModelFactory @Inject constructor(
    private val dataSourceFactory: MovieDataSourceFactory,
    private val boundaryCallback: MovieBoundaryCallback
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            MovieViewModel(dataSourceFactory, boundaryCallback) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}