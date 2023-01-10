package com.demo.securewifianalyzer.admob

import com.demo.securewifianalyzer.app.log
import com.demo.securewifianalyzer.base.BasePage
import com.demo.securewifianalyzer.config.LocalConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShowFullAd(
    private val basePage: BasePage,
    private val type:String,
    private val closeAd:()->Unit
) {

    fun showFull(call:(finish:Boolean)->Unit){
        val adByType = LoadAdmobImpl.getAdByType(type)
        if (null!=adByType){
            if (LoadAdmobImpl.showingFull||!basePage.resume){
                call.invoke(true)
                return
            }
            "start show $type ad".log()
            call.invoke(false)
            when(adByType){
                is AppOpenAd->{
                    adByType.fullScreenContentCallback=callback
                    adByType.show(basePage)
                }
                is InterstitialAd->{
                    adByType.fullScreenContentCallback=callback
                    adByType.show(basePage)
                }
            }
        }else{
            if(type==LocalConfig.SWAN_FUNCTION_IN||type==LocalConfig.SWAN_WIFICON_IN){
                LoadAdmobImpl.load(type)
                call.invoke(true)
            }
        }
    }

    private val callback=object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            LoadAdmobImpl.showingFull=false
            closeAd()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            LoadAdmobImpl.showingFull=true
            LoadAdmobImpl.removeAdByType(type)
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            LoadAdmobImpl.showingFull=false
            LoadAdmobImpl.removeAdByType(type)
            closeAd()
        }

        private fun closeAd(){
            if (type!=LocalConfig.SWAN_START){
                LoadAdmobImpl.load(type)
            }
            GlobalScope.launch(Dispatchers.Main) {
                delay(200L)
                if (basePage.resume){
                    closeAd.invoke()
                }
            }
        }
    }
}