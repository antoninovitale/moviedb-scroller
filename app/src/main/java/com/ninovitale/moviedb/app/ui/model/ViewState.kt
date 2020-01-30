package com.ninovitale.moviedb.app.ui.model

sealed class ViewState {
    data class Loading(val isLoading: Boolean) : ViewState()

    //TODO error could be more complex and contain code, message, etc...
    data class Error(val message: String? = null) : ViewState()
}