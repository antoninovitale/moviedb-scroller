package com.ninovitale.moviedb.app.tools

import android.util.Log
import com.ninovitale.moviedb.app.BuildConfig

@Suppress("unused")
object DebugLog {
    fun log(tag: String?, message: String? = null, e: Throwable?) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message ?: "An error occurred!", e)
        }
    }

    fun log(tag: String?, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }
}