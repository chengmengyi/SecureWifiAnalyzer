package com.demo.securewifianalyzer.manager

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.demo.securewifianalyzer.page.net_test.NetTestPage
import com.demo.securewifianalyzer.page.security.WifiSecurityPage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ActivityCallback {
    var isFront=true
    private var reload=false
    private var job: Job?=null

    fun register(application: Application){
        application.registerActivityLifecycleCallbacks(callback)
    }

    private val callback=object : Application.ActivityLifecycleCallbacks{
        private var pages=0
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {
            pages++
            job?.cancel()
            job=null
            if (pages==1){
                isFront=true
            }
        }

        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {
            pages--
            if (pages<=0){
                isFront=false
                job= GlobalScope.launch {
                    ActivityUtils.finishActivity(WifiSecurityPage::class.java)
                    ActivityUtils.finishActivity(NetTestPage::class.java)
                    delay(3000L)
                    reload=true
//                    ActivityUtils.finishActivity(MainActivity::class.java)
//                    ActivityUtils.finishActivity(AdActivity::class.java)
                }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }
}