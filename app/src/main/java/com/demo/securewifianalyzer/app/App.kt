package com.demo.securewifianalyzer.app

import android.app.Application
import com.demo.securewifianalyzer.manager.ActivityCallback
import com.tencent.mmkv.MMKV
import org.litepal.LitePal

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        LitePal.initialize(this)
        MMKV.initialize(this)
        ActivityCallback.register(this)
    }
}