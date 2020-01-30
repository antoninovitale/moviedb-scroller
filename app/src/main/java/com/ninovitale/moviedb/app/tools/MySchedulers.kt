package com.ninovitale.moviedb.app.tools

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object MySchedulers {
    val uiThread: Scheduler
        get() = AndroidSchedulers.mainThread()

    val ioThread: Scheduler
        get() = Schedulers.io()

    val computationThread: Scheduler
        get() = Schedulers.computation()
}