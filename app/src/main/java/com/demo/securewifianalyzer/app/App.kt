package com.demo.securewifianalyzer.app

import android.app.Application
import com.demo.securewifianalyzer.config.FireConfig
import com.demo.securewifianalyzer.manager.ActivityCallback
import com.tencent.mmkv.MMKV
import org.litepal.LitePal

lateinit var mApp:App
class App:Application() {
    override fun onCreate() {
        super.onCreate()
        mApp=this
        LitePal.initialize(this)
        MMKV.initialize(this)
        ActivityCallback.register(this)
        FireConfig.readFire()
        AcCallback.register(this)
    }
}