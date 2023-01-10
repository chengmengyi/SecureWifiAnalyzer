package com.demo.securewifianalyzer.admob

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.app.show
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class ShowDialogNativeAd(
    private val type:String,
    private val view: View,
) {
    
    fun showNative(ad: NativeAd){
        val viewNative = view.findViewById<NativeAdView>(R.id.native_view)
        viewNative.iconView=view.findViewById(R.id.native_logo)
        (viewNative.iconView as ImageFilterView).setImageDrawable(ad.icon?.drawable)

        viewNative.callToActionView=view.findViewById(R.id.native_install)
        (viewNative.callToActionView as AppCompatTextView).text= ad.callToAction

        viewNative.bodyView=view.findViewById(R.id.native_desc)
        (viewNative.bodyView as AppCompatTextView).text=ad.body

        viewNative.headlineView=view.findViewById(R.id.native_title)
        (viewNative.headlineView as AppCompatTextView).text=ad.headline

        viewNative.setNativeAd(ad)
        view.findViewById<AppCompatImageView>(R.id.iv_default).show(false)

        LoadAdmobImpl.removeAdByType(type)
        LoadAdmobImpl.load(type)
        AdReloadManager.setBool(type,false)
    }
}