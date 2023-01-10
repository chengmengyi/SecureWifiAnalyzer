package com.demo.securewifianalyzer.page.security

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.util.Log
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.admob.ShowFullAd
import com.demo.securewifianalyzer.base.BasePage
import com.demo.securewifianalyzer.config.LocalConfig
import com.demo.securewifianalyzer.manager.WifiUtils
import kotlinx.coroutines.*
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.SpeedTestReport

import fr.bmartel.speedtest.model.SpeedTestError

import fr.bmartel.speedtest.inter.ISpeedTestListener
import kotlinx.android.synthetic.main.activity_wifi_security.*


class WifiSecurityPage:BasePage() {
    private var job:Job?=null
    private var timeOutJob: Job?=null
    private var resultIntent:Intent?=null
    private val speedTestSocket = SpeedTestSocket()
    private var objectAnimator: ObjectAnimator?=null
    private val showFullAd by lazy { ShowFullAd(this, LocalConfig.SWAN_FUNCTION_IN){ toNextPage() } }

    override fun layout(): Int = R.layout.activity_wifi_security

    override fun view() {
        immersionBar.statusBarView(top_view).statusBarDarkFont(false).init()
        startAnimator()
        startScan()
        iv_back.setOnClickListener { finish() }
    }

    private fun startScan(){
        val instance = WifiUtils.getInstance(this@WifiSecurityPage)
        val currentWifiName = instance?.getCurrentWifiName()?:""
        val wifiIp = instance?.getWifiIp()?:""
        val maxSpeed = instance?.getMaxSpeed()?:""
        val wifiMac = instance?.getWifiMac()?:""
        var speed=0L

        val listener = object : ISpeedTestListener {
            override fun onCompletion(report: SpeedTestReport) {
                speed = report.transferRateOctet.toLong()
                toResult(speed, currentWifiName, wifiIp, maxSpeed, wifiMac)
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                toResult(speed, currentWifiName, wifiIp, maxSpeed, wifiMac)
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {}
        }
        speedTestSocket.addSpeedTestListener(listener)

        job=GlobalScope.launch {
            speedTestSocket.startDownload("http://ipv4.appliwave.testdebit.info/5M/5M.zip")
        }

        timeOutJob=GlobalScope.launch {
            delay(10000L)
            speedTestSocket.clearListeners()
            withContext(Dispatchers.Main){
                toResult(speed,currentWifiName, wifiIp, maxSpeed, wifiMac)
            }
        }
    }

    private fun toResult(transferRateOctet:Long,currentWifiName:String,wifiIp:String,maxSpeed:String,wifiMac:String){
        resultIntent = Intent(this, WifiSecurityResultPage::class.java).apply {
            putExtra("speed",transferRateOctet)
            putExtra("wifiName",currentWifiName)
            putExtra("wifiIp",wifiIp)
            putExtra("maxSpeed",maxSpeed)
            putExtra("wifiMac",wifiMac)
        }
        runOnUiThread {
            showFullAd.showFull {
                stopAnimator()
                speedTestSocket.clearListeners()
                speedTestSocket.closeSocket()
                finishJob()
                if (it){
                    toNextPage()
                }
            }
        }
    }

    private fun toNextPage(){
        if (null!=resultIntent){
            startActivity(resultIntent)
        }
        finish()
    }

    private fun startAnimator(){
        objectAnimator=ObjectAnimator.ofFloat(iv_wifi_security, "rotation", 0f, 360f).apply {
            duration=1000L
            repeatCount= ValueAnimator.INFINITE
            repeatMode=ObjectAnimator.RESTART
            start()
        }
    }

    private fun stopAnimator() {
        objectAnimator?.removeAllUpdateListeners()
        objectAnimator?.cancel()
        objectAnimator = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAnimator()
        speedTestSocket.clearListeners()
        speedTestSocket.closeSocket()
        finishJob()
    }

    private fun finishJob(){
        job?.cancel()
        job=null
        timeOutJob?.cancel()
        timeOutJob=null
    }
}