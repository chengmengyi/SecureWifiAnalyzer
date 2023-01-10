package com.demo.securewifianalyzer.app

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.demo.securewifianalyzer.page.HomePage
import com.demo.securewifianalyzer.page.LaunchPage
import com.demo.securewifianalyzer.page.net_test.NetTestPage
import com.demo.securewifianalyzer.page.security.WifiSecurityPage
import com.google.android.gms.ads.AdActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AcCallback {

    var hotLoad=false
    private var job: Job?=null

    fun register(application: Application){
        application.registerActivityLifecycleCallbacks(object :Application.ActivityLifecycleCallbacks{
            private var pages=0
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {

            }

            override fun onActivityStarted(p0: Activity) {
                pages++
                job?.cancel()
                job=null
                if (pages==1){
                    if (hotLoad&&ActivityUtils.isActivityExistsInStack(HomePage::class.java)){
                        p0.startActivity(Intent(p0, LaunchPage::class.java))
                    }
                    hotLoad=false
                }
            }

            override fun onActivityResumed(p0: Activity) {

            }

            override fun onActivityPaused(p0: Activity) {

            }

            override fun onActivityStopped(p0: Activity) {
                pages--
                if (pages<=0){
                    ActivityUtils.finishActivity(NetTestPage::class.java)
                    ActivityUtils.finishActivity(WifiSecurityPage::class.java)
                    job= GlobalScope.launch {
                        delay(3000L)
                        hotLoad=true
                        ActivityUtils.finishActivity(LaunchPage::class.java)
                        ActivityUtils.finishActivity(AdActivity::class.java)
                    }
                }
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

            }

            override fun onActivityDestroyed(p0: Activity) {

            }

        })
    }
}