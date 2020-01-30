package com.ninovitale.moviedb.app.tools

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.ninovitale.moviedb.app.R
import javax.inject.Singleton

@Singleton
class MyImageLoader {
    fun loadImage(context: Context, url: String?, view: ImageView) {
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_placeholder)
            .fallback(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .fitCenter()
            .into(view)
    }
}