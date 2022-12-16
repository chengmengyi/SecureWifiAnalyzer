package com.demo.securewifianalyzer.page.net_test

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.util.Log
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.app.getDelay
import com.demo.securewifianalyzer.base.BasePage
import com.demo.securewifianalyzer.manager.WifiUtils
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import kotlinx.android.synthetic.main.activity_net_test.*
import kotlinx.coroutines.*

class NetTestPage:BasePage() {
    private var wifiName=""
    private var downloadJob: Job?=null
    private var timeOutJob: Job?=null
    private val speedTestSocket = SpeedTestSocket()
    private var objectAnimator: ObjectAnimator?=null

    override fun layout(): Int = R.layout.activity_net_test

    override fun view() {
        immersionBar.statusBarView(top_view).statusBarDarkFont(false).init()
        startAnimator()
        testNet()
        iv_back.setOnClickListener { finish() }
    }

    private fun testNet(){
        var speed=0L
        var googleDelay=0
        var twitterDelay=0
        var facebookDelay=0
        val listener = object : ISpeedTestListener {
            override fun onCompletion(report: SpeedTestReport) {
                speed = report.transferRateOctet.toLong()
                toResult(speed, googleDelay, twitterDelay, facebookDelay)
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                toResult(speed, googleDelay, twitterDelay, facebookDelay)
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {}
        }
        speedTestSocket.addSpeedTestListener(listener)

        downloadJob=GlobalScope.launch {
            googleDelay = getDelay("www.google.com")
            twitterDelay = getDelay("twitter.com")
            facebookDelay = getDelay("www.facebook.com")
            wifiName=WifiUtils.getInstance(this@NetTestPage)?.getCurrentWifiName()?:""

            speedTestSocket.startDownload("http://ipv4.appliwave.testdebit.info/5M/5M.zip")
        }

        timeOutJob=GlobalScope.launch {
            delay(10000L)
            speedTestSocket.clearListeners()
            withContext(Dispatchers.Main){
                toResult(speed, googleDelay, twitterDelay, facebookDelay)
            }
        }
    }

    private fun toResult(speed:Long,googleDelay:Int,twitterDelay:Int,facebookDelay:Int){
        val apply = Intent(this, NetTestResultPage::class.java).apply {
            putExtra("speed", speed)
            putExtra("googleDelay", googleDelay)
            putExtra("twitterDelay", twitterDelay)
            putExtra("facebookDelay", facebookDelay)
            putExtra("wifiName", wifiName)
        }
        startActivity(apply)
        finish()
    }

    private fun startAnimator(){
        objectAnimator=ObjectAnimator.ofFloat(iv_net_test, "rotation", 0f, 360f).apply {
            duration=1000L
            repeatCount= ValueAnimator.INFINITE
            repeatMode=ObjectAnimator.RESTART
            start()
        }
    }

    private fun stopAnimator(){
        objectAnimator?.removeAllUpdateListeners()
        objectAnimator?.cancel()
        objectAnimator=null
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAnimator()
        speedTestSocket.clearListeners()
        speedTestSocket.closeSocket()
        downloadJob?.cancel()
        downloadJob=null
        timeOutJob?.cancel()
        timeOutJob=null
    }
}