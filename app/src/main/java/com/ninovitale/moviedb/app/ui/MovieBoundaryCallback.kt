package com.ninovitale.moviedb.app.ui

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.ninovitale.moviedb.app.domain.CompletionEvent
import com.ninovitale.moviedb.app.domain.MovieUseCase
import com.ninovitale.moviedb.app.ui.model.Movie
import com.ninovitale.moviedb.app.ui.model.ViewState
import javax.inject.Inject

class MovieBoundaryCallback @Inject constructor(
    private val useCase: MovieUseCase
) : PagedList.BoundaryCallback<Movie>() {
    private var viewState: MutableLiveData<ViewState>? = null
    private var pageToRequest = 1
    private var blockOtherRequests = false
    private var noMoreData = false

    fun setLiveData(viewState: MutableLiveData<ViewState>) {
        this.viewState = viewState
    }

    override fun onZeroItemsLoaded() {
        requestAndSaveData()
    }

    override fun onItemAtEndLoaded(itemAtEnd: Movie) {
        requestAndSaveData()
    }

    override fun onItemAtFrontLoaded(itemAtFront: Movie) {}

    private fun requestAndSaveData() {
        if (blockOtherRequests || noMoreData) return

        useCase.loadMovies(pageToRequest) {
            when (it) {
                CompletionEvent.Loading -> viewState?.postValue(ViewState.Loading(true))
                is CompletionEvent.Loaded -> {
                    viewState?.postValue(ViewState.Loading(false))
                    if (it.nextPage == null) noMoreData = true
                    else pageToRequest++
                }
                is CompletionEvent.Error -> viewState?.postValue(ViewState.Error(it.throwable.message))
            }
        }
    }
}