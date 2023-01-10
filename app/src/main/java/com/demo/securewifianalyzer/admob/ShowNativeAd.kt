package com.demo.securewifianalyzer.admob

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.SizeUtils
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.app.show
import com.demo.securewifianalyzer.base.BasePage
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.*

class ShowNativeAd(
    private val basePage: BasePage,
    private val type:String
) {
    private var job:Job?=null
    private var lastNative:NativeAd?=null
    
    fun startShow(){
        LoadAdmobImpl.load(type)
        endShow()
        job= GlobalScope.launch(Dispatchers.Main) {
            delay(300L)
            if (!basePage.resume){
                return@launch
            }
            while (true) {
                if (!isActive) {
                    break
                }

                val adByType = LoadAdmobImpl.getAdByType(type)
                if(basePage.resume && null!=adByType && adByType is NativeAd){
                    cancel()
                    lastNative?.destroy()
                    lastNative=adByType
                    showNative(adByType)
                }

                delay(1000L)
            }
        }
    }
    
    private fun showNative(ad:NativeAd){
        val viewNative = basePage.findViewById<NativeAdView>(R.id.native_view)
        viewNative.iconView=basePage.findViewById(R.id.native_logo)
        (viewNative.iconView as ImageFilterView).setImageDrawable(ad.icon?.drawable)

        viewNative.callToActionView=basePage.findViewById(R.id.native_install)
        (viewNative.callToActionView as AppCompatTextView).text= ad.callToAction

        viewNative.mediaView=basePage.findViewById(R.id.native_cover)
        ad.mediaContent?.let {
            viewNative.mediaView?.apply {
                setMediaContent(it)
                setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View?, outline: Outline?) {
                        if (view == null || outline == null) return
                        outline.setRoundRect(
                            0,
                            0,
                            view.width,
                            view.height,
                            SizeUtils.dp2px(6F).toFloat()
                        )
                        view.clipToOutline = true
                    }
                }
            }
        }

        viewNative.bodyView=basePage.findViewById(R.id.native_desc)
        (viewNative.bodyView as AppCompatTextView).text=ad.body

        viewNative.headlineView=basePage.findViewById(R.id.native_title)
        (viewNative.headlineView as AppCompatTextView).text=ad.headline

        viewNative.setNativeAd(ad)
        basePage.findViewById<AppCompatImageView>(R.id.iv_default).show(false)

        LoadAdmobImpl.removeAdByType(type)
        LoadAdmobImpl.load(type)
        AdReloadManager.setBool(type,false)
    }
    
    fun endShow(){
        job?.cancel()
        job=null
    }
}