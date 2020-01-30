package com.ninovitale.moviedb.app.data

import androidx.paging.DataSource

interface MovieRepository {
    fun read(): DataSource.Factory<Int, MovieDbItem>
    fun write(movies: List<MovieDbItem>)
    fun clear()
}

class MovieRepositoryDatabase(private val dao: MovieDao) : MovieRepository {
    override fun read(): DataSource.Factory<Int, MovieDbItem> {
        return dao.loadPage()
    }

    override fun write(movies: List<MovieDbItem>) {
        dao.insertAll(movies)
    }

    override fun clear() {
        dao.clear()
    }
}