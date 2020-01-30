package com.ninovitale.moviedb.app.ui

import android.os.Bundle
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SnapHelper
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.ninovitale.moviedb.app.MovieApp
import com.ninovitale.moviedb.app.R
import com.ninovitale.moviedb.app.tools.MyImageLoader
import com.ninovitale.moviedb.app.ui.model.ViewState
import com.ninovitale.moviedb.app.viewmodel.MovieViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    @Inject
    internal lateinit var imageLoader: MyImageLoader
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private var viewModel: MovieViewModel? = null
    private var recyclerViewAdapter: MoviePagedListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MovieApp).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupRecyclerView()
        setupObservers()
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun onDestroy() {
        swipeRefreshLayout.setOnRefreshListener(null)
        recyclerViewAdapter = null
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL,
            false
        )
        val snapHelper: SnapHelper = GravitySnapHelper(Gravity.TOP)
        snapHelper.attachToRecyclerView(recycler_view)
        recyclerViewAdapter = MoviePagedListAdapter(imageLoader)
        recycler_view.adapter = recyclerViewAdapter
    }

    private fun setupObservers() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(MovieViewModel::class.java)
        viewModel?.getViewState()?.observe(this, Observer { state ->
            when (state) {
                is ViewState.Loading -> {
                    if (!state.isLoading) swipeRefreshLayout.isRefreshing = false
                    progress_view.visibility = if (state.isLoading) VISIBLE else GONE
                }
                is ViewState.Error -> {
                    swipeRefreshLayout.isRefreshing = false
                    progress_view.visibility = GONE
                    //TODO notify user in a better way
                    state.message?.let {
                        Snackbar.make(frameLayout, it, LENGTH_SHORT).show()
                    } ?: state.message?.let {
                        Snackbar.make(frameLayout, R.string.error_message, LENGTH_SHORT).show()
                    }
                }
            }
        })
        viewModel?.getMovies()?.observe(this, Observer { recyclerViewAdapter?.submitList(it) })
    }

    override fun onRefresh() {
        recyclerViewAdapter?.currentList?.dataSource?.invalidate()
    }
}