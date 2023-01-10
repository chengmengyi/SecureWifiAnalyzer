package com.demo.securewifianalyzer.app

import android.app.Application
import com.demo.securewifianalyzer.manager.ActivityCallback
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.tencent.mmkv.MMKV
import org.litepal.LitePal

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        LitePal.initialize(this)
        MMKV.initialize(this)
        ActivityCallback.register(this)
    }
}