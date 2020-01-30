package com.ninovitale.moviedb.app.utils

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstancePostProcessor
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class TestSchedulerExtension : TestInstancePostProcessor {
    override fun postProcessTestInstance(testInstance: Any, context: ExtensionContext?) {
        val scheduler = TestScheduler()

        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
        RxAndroidPlugins.setMainThreadSchedulerHandler { scheduler }


        RxJavaPlugins.setInitIoSchedulerHandler { scheduler }
        RxJavaPlugins.setIoSchedulerHandler { scheduler }

        RxJavaPlugins.setInitComputationSchedulerHandler { scheduler }
        RxJavaPlugins.setComputationSchedulerHandler { scheduler }

        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler }
        RxJavaPlugins.setNewThreadSchedulerHandler { scheduler }

        RxJavaPlugins.setInitSingleSchedulerHandler { scheduler }
        RxJavaPlugins.setSingleSchedulerHandler { scheduler }

        testInstance::class.memberProperties
            .first { it.findAnnotation<InjectTestScheduler>() != null }
            .let { it as KMutableProperty<*> }
            .run {
                setter.call(testInstance, scheduler)
            }
    }
}

annotation class InjectTestScheduler