package com.demo.securewifianalyzer.page

import android.animation.ValueAnimator
import android.content.Intent
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.blankj.utilcode.util.ActivityUtils
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.app.isLocationProviderEnabled
import com.demo.securewifianalyzer.base.BasePage
import com.demo.securewifianalyzer.dialog.LocationDialog
import kotlinx.android.synthetic.main.activity_main.*

class LaunchPage : BasePage() {
    private var launchAnimator:ValueAnimator?=null

    override fun layout(): Int = R.layout.activity_main

    override fun view() {
        start()
    }

    private fun start(){
        launchAnimator=ValueAnimator.ofInt(0, 100).apply {
            duration = 3000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                launch_progress.progress = progress
            }
            doOnEnd { hasPermission() }
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