package com.demo.securewifianalyzer.admob

import com.demo.securewifianalyzer.app.log
import com.demo.securewifianalyzer.app.mApp
import com.demo.securewifianalyzer.bean.ConfigAdBean
import com.demo.securewifianalyzer.bean.ResultAdBean
import com.demo.securewifianalyzer.config.FireConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import org.json.JSONObject

abstract class LoadAdmob {
    fun loadAdmob(type:String, configAdBean: ConfigAdBean, result:(resultAdBean: ResultAdBean)->Unit){
        "start load admob,${configAdBean.toString()}".log()
        when(configAdBean.swan_t){
            "open"->loadOpen(configAdBean, result)
            "interstitial"->loadInterstitial(configAdBean, result)
            "native"->loadNative(configAdBean, result)
        }
    }

    private fun loadOpen(configAdBean: ConfigAdBean, result:(resultAdBean: ResultAdBean)->Unit){
        AppOpenAd.load(
            mApp,
            configAdBean.swan_id,
            AdRequest.Builder().build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback(){
                override fun onAdLoaded(p0: AppOpenAd) {
                    result.invoke(ResultAdBean(ad = p0, time = System.currentTimeMillis()))
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    result.invoke(ResultAdBean(fail = p0.message))
                }
            }
        )
    }

    private fun loadInterstitial(configAdBean: ConfigAdBean,result:(resultAdBean:ResultAdBean)->Unit){
        InterstitialAd.load(
            mApp,
            configAdBean.swan_id,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    result.invoke(ResultAdBean(fail = p0.message))
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    result.invoke(ResultAdBean(ad = p0, time = System.currentTimeMillis()))
                }
            }
        )
    }

    private fun loadNative(configAdBean: ConfigAdBean,result:(resultAdBean:ResultAdBean)->Unit){
        AdLoader.Builder(
            mApp,
            configAdBean.swan_id,
        ).forNativeAd {
            result.invoke(ResultAdBean(ad = it, time = System.currentTimeMillis()))
        }
            .withAdListener(object : AdListener(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    result.invoke(ResultAdBean(fail = p0.message))
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(
                        NativeAdOptions.ADCHOICES_BOTTOM_LEFT
                    )
                    .build()
            )
            .build()
            .loadAd(AdRequest.Builder().build())
    }

    protected fun parseAdList(type:String):List<ConfigAdBean>{
        val list= arrayListOf<ConfigAdBean>()
        try {
            val jsonArray = JSONObject(FireConfig.getAdStr()).getJSONArray(type)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    ConfigAdBean(
                        jsonObject.optString("swan_s"),
                        jsonObject.optInt("swan_l"),
                        jsonObject.optString("swan_t"),
                        jsonObject.optString("swan_id"),
                    )
                )
            }
        }catch (e:Exception){
        }
        return list.filter { it.swan_s == "admob" }.sortedByDescending { it.swan_l }
    }
}