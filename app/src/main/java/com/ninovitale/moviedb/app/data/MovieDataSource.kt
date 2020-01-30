package com.ninovitale.moviedb.app.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.ninovitale.moviedb.app.domain.CompletionEvent
import com.ninovitale.moviedb.app.domain.MovieUseCase
import com.ninovitale.moviedb.app.ui.model.ViewState
import javax.inject.Inject

class MovieDataSource constructor(
    private val useCase: MovieUseCase
) : PageKeyedDataSource<Int, MovieDbItem>() {
    private val viewState: MutableLiveData<ViewState> = MutableLiveData()

    fun getViewState(): MutableLiveData<ViewState> = viewState

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, MovieDbItem>
    ) {

        useCase.loadMovies {
            when (it) {
                CompletionEvent.Loading -> viewState.postValue(ViewState.Loading(true))
                is CompletionEvent.Loaded -> {
                    viewState.postValue(ViewState.Loading(false))
                    callback.onResult(it.items, null, 2)
                }
                is CompletionEvent.Error -> viewState.postValue(ViewState.Error(it.throwable.message))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MovieDbItem>) {
        val pageNumber = params.key

        useCase.loadMovies(pageNumber) {
            when (it) {
                CompletionEvent.Loading -> viewState.postValue(ViewState.Loading(true))
                is CompletionEvent.Loaded -> {
                    viewState.postValue(ViewState.Loading(false))
                    callback.onResult(it.items, it.nextPage)
                }
                is CompletionEvent.Error -> viewState.postValue(ViewState.Error(it.throwable.message))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MovieDbItem>) {}
}

class MovieDataSourceFactory @Inject constructor(
    private val useCase: MovieUseCase
) : DataSource.Factory<Int, MovieDbItem>() {

    private val liveData = MutableLiveData<MovieDataSource>()

    override fun create(): DataSource<Int, MovieDbItem> {
        val source = MovieDataSource(useCase)
        liveData.postValue(source)
        return source
    }

    fun getLiveData(): MutableLiveData<MovieDataSource> = liveData

    companion object {
        private const val PAGE_SIZE = 20

        fun pagedListConfig() = PagedList.Config.Builder()
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setPageSize(PAGE_SIZE)
            .setEnablePlaceholders(true)
            .build()
    }
}