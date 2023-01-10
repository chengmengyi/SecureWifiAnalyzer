package com.demo.securewifianalyzer.page.security

import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.admob.AdReloadManager
import com.demo.securewifianalyzer.admob.ShowNativeAd
import com.demo.securewifianalyzer.app.byteToMB
import com.demo.securewifianalyzer.base.BasePage
import com.demo.securewifianalyzer.config.LocalConfig
import kotlinx.android.synthetic.main.activity_wifi_security_result.*
import kotlinx.android.synthetic.main.layout_wifi_security_info.*
import kotlinx.android.synthetic.main.layout_wifi_security_speed.*
import kotlinx.android.synthetic.main.layout_wifi_security_speed_level.*

class WifiSecurityResultPage:BasePage() {
    private val showNativeAd by lazy { ShowNativeAd(this,LocalConfig.SWAN_FUNC_NA) }

    override fun layout(): Int = R.layout.activity_wifi_security_result

    override fun view() {
        immersionBar.statusBarView(top_view).init()
        setWifiInfo()
        iv_back.setOnClickListener { finish() }
    }

    private fun setWifiInfo(){
        val wifiName = intent.getStringExtra("wifiName") ?: ""
        val wifiIp = intent.getStringExtra("wifiIp") ?: ""
        val maxSpeed = intent.getStringExtra("maxSpeed") ?: ""
        val wifiMac = intent.getStringExtra("wifiMac") ?: ""
        tv_wifi_name.text=wifiName
        tv_max_speed.text=maxSpeed
        tv_wifi_ip.text=wifiIp
        tv_wifi_mac.text=wifiMac
        val speed = intent.getLongExtra("speed", 0L)
        tv_speed.text= "${byteToMB(speed)}/S"
        when(speed/1024){
            //2
            in 10..80->{
                iv_speed_1.isSelected=true
                iv_speed_2.isSelected=true
            }
            //3
            in 80..120->{
                iv_speed_1.isSelected=true
                iv_speed_2.isSelected=true
                iv_speed_3.isSelected=true
            }
            //4
            in 120..Int.MAX_VALUE->{
                iv_speed_1.isSelected=true
                iv_speed_2.isSelected=true
                iv_speed_3.isSelected=true
                iv_speed_4.isSelected=true
            }
            //1
            else->{
                if(speed>0L){
                    iv_speed_1.isSelected=true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (AdReloadManager.canReload(LocalConfig.SWAN_FUNC_NA)){
            showNativeAd.startShow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AdReloadManager.setBool(LocalConfig.SWAN_FUNC_NA,true)
        showNativeAd.endShow()
    }
}