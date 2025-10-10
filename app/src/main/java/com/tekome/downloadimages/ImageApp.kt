package com.tekome.downloadimages

import android.app.Activity
import android.app.Application
import android.os.Bundle
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ImageApp :
    Application(),
    Application.ActivityLifecycleCallbacks {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("onCreate start")
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(
        activity: Activity,
        bundle: Bundle?,
    ) {
        Timber.i("onActivityCreated $activity")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.i("onActivityDestroyed $activity")
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.i("onActivityPaused $activity")
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.i("onActivityResumed $activity")
    }

    override fun onActivitySaveInstanceState(
        activity: Activity,
        bundle: Bundle,
    ) {
        Timber.i("onActivitySaveInstanceState $activity")
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.i("onActivityStarted $activity")
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.i("onActivityStopped $activity")
    }
}
