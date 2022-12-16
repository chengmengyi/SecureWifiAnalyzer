package com.demo.securewifianalyzer.dialog

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.app.hasOverlayPermission
import com.demo.securewifianalyzer.base.BaseDialog
import com.demo.securewifianalyzer.bean.WifiInfoBean
import com.demo.securewifianalyzer.manager.WifiUtils
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.dialog_connect_wifi.*

class ConnectWifiDialog(
    private val wifiInfoBean: WifiInfoBean,
    private val connectCallback:(pwd:String)->Unit
):BaseDialog() {

    override fun layout(): Int = R.layout.dialog_connect_wifi

    override fun view() {
        tv_wifi_name.text=wifiInfoBean.name
        val pwd = MMKV.defaultMMKV().decodeString(wifiInfoBean.name) ?: ""
        if (pwd.isNotEmpty()){
            et_wifi_pwd.setText(pwd)
        }

        tv_cancel.setOnClickListener { dismiss() }

        tv_connect_wifi.setOnClickListener {
            connectWifi()
        }

        iv_show_pwd.setOnClickListener {
            iv_show_pwd.isSelected=!iv_show_pwd.isSelected
            if(iv_show_pwd.isSelected){
                et_wifi_pwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }else{
                et_wifi_pwd.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    private fun connectWifi(){
        if(!hasOverlayPermission(requireContext())){
            val intent= Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${requireContext().packageName}"))
            startActivityForResult(intent, 101)
            return
        }

        val pwd = et_wifi_pwd.text.toString().trim()
        if (pwd.length<8){
            return
        }
        dismiss()
        connectCallback.invoke(pwd)
    }
}