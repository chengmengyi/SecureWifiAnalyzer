package com.demo.securewifianalyzer.page

import android.animation.ValueAnimator
import android.content.Intent
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.blankj.utilcode.util.ActivityUtils
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.admob.AdReloadManager
import com.demo.securewifianalyzer.admob.LoadAdmobImpl
import com.demo.securewifianalyzer.admob.ShowFullAd
import com.demo.securewifianalyzer.app.isLocationProviderEnabled
import com.demo.securewifianalyzer.base.BasePage
import com.demo.securewifianalyzer.config.LocalConfig
import com.demo.securewifianalyzer.dialog.LocationDialog
import kotlinx.android.synthetic.main.activity_main.*

class LaunchPage : BasePage() {
    private var launchAnimator:ValueAnimator?=null
    private val showOpenAd by lazy { ShowFullAd(this,LocalConfig.SWAN_START){ hasPermission() } }

    override fun layout(): Int = R.layout.activity_main

    override fun view() {
        AdReloadManager.resetAll()
        preLoadAd()
        start()
    }

    private fun preLoadAd(){
        LoadAdmobImpl.load(LocalConfig.SWAN_START)
        LoadAdmobImpl.load(LocalConfig.SWAN_HOME_NA)
        LoadAdmobImpl.load(LocalConfig.SWAN_WIFI_NA)
        LoadAdmobImpl.load(LocalConfig.SWAN_FUNC_NA)
        LoadAdmobImpl.load(LocalConfig.SWAN_FUNCTION_IN)
        LoadAdmobImpl.load(LocalConfig.SWAN_WIFICON_IN)
    }

    private fun start(){
        launchAnimator=ValueAnimator.ofInt(0, 100).apply {
            duration = 10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                launch_progress.progress = progress
                val pro = (10 * (progress / 100.0F)).toInt()
                if (pro in 2..9){
                    showOpenAd.showFull { finish->
                        launch_progress.progress = 100
                        stop()
                        if (finish){
                            hasPermission()
                        }
                    }
                }else if (pro>=10){
                    hasPermission()
                }
            }
            start()
        }
    }

    private fun hasPermission(){
        if(isLocationProviderEnabled()){
            showHomePage()
        }else{
            LocationDialog{
                if(it){
                    showHomePage()
                }else{
                    finish()
                }
            }.show(supportFragmentManager,"LocationDialog")
        }
    }

    private fun showHomePage(){
        if (!ActivityUtils.isActivityExistsInStack(HomePage::class.java)){
            startActivity(Intent(this,HomePage::class.java))
        }
        finish()
    }

    private fun stop(){
        launchAnimator?.removeAllUpdateListeners()
        launchAnimator?.cancel()
        launchAnimator=null
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        launchAnimator?.resume()
    }

    override fun onPause() {
        super.onPause()
        launchAnimator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }
}