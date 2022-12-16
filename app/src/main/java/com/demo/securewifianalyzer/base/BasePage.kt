package com.demo.securewifianalyzer.base

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import org.greenrobot.eventbus.EventBus

abstract class BasePage:AppCompatActivity() {
    lateinit var immersionBar: ImmersionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayMetrics()
        setContentView(layout())
        immersionBar= ImmersionBar.with(this).apply {
            statusBarAlpha(0f)
            autoDarkModeEnable(true)
            statusBarDarkFont(true)
            init()
        }
        if(initEvent()){
            EventBus.getDefault().register(this)
        }
        view()
    }

    abstract fun layout():Int

    abstract fun view()

    open fun initEvent()=false

    private fun displayMetrics(){
        val metrics: DisplayMetrics = resources.displayMetrics
        val td = metrics.heightPixels / 760f
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
    }

    override fun onDestroy() {
        super.onDestroy()
        if(initEvent()){
            EventBus.getDefault().unregister(this)
        }
    }
}
