package com.ninovitale.moviedb.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ninovitale.moviedb.app.R
import com.ninovitale.moviedb.app.tools.MyImageLoader
import com.ninovitale.moviedb.app.ui.model.Movie
import kotlinx.android.synthetic.main.element_movie.view.*

class MoviePagedListAdapter(private val imageLoader: MyImageLoader) :
    PagedListAdapter<Movie, MovieViewHolder>(MovieDiffCallback()) {

    private class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.element_movie, parent, false
        )
        return MovieViewHolder(v)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            imageLoader.loadImage(holder.itemView.context, it.posterPath, holder.itemView.img)
            holder.itemView.title.text = it.originalTitle
            holder.itemView.overview.text = it.overview
            holder.itemView.rating.text = it.rating
            holder.itemView.release_date.text = it.releaseDate
        }
    }
}