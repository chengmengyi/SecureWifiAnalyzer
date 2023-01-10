package com.demo.securewifianalyzer.page

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.adapter.WifiListAdapter
import com.demo.securewifianalyzer.app.show
import com.demo.securewifianalyzer.base.BasePage
import com.demo.securewifianalyzer.bean.WifiInfoBean
import com.demo.securewifianalyzer.dialog.ConnectWifiDialog
import com.demo.securewifianalyzer.manager.WifiUtils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_connected_wifi.*
import kotlinx.android.synthetic.main.layout_current_wifi_info.*
import kotlinx.android.synthetic.main.layout_current_wifi_info.llc_current_wifi
import kotlinx.android.synthetic.main.layout_other_wifi.*
import android.net.wifi.WifiManager
import android.net.ConnectivityManager
import android.content.IntentFilter
import android.provider.Settings
import com.blankj.utilcode.util.SizeUtils
import com.demo.securewifianalyzer.admob.AdReloadManager
import com.demo.securewifianalyzer.admob.ShowFullAd
import com.demo.securewifianalyzer.admob.ShowNativeAd
import com.demo.securewifianalyzer.app.isLocationProviderEnabled
import com.demo.securewifianalyzer.app.log
import com.demo.securewifianalyzer.app.toast
import com.demo.securewifianalyzer.broadcast.WifiStateReceiver
import com.demo.securewifianalyzer.config.LocalConfig
import com.demo.securewifianalyzer.eventbus.EventBean
import com.demo.securewifianalyzer.eventbus.EventCode
import com.demo.securewifianalyzer.manager.CurrentWifiManager
import com.demo.securewifianalyzer.page.net_test.NetTestPage
import com.demo.securewifianalyzer.page.net_test.TestRecordPage
import com.demo.securewifianalyzer.page.security.WifiSecurityPage
import com.demo.securewifianalyzer.page.set.SetPage
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.layout_home_func.*
import org.greenrobot.eventbus.Subscribe

class HomePage:BasePage(), OnRefreshListener {
    private var wifiPwd=""
    private var scrollY=0
    private var clickWifiInfoBean:WifiInfoBean?=null
    private var wifiStateReceiver: WifiStateReceiver?=null
    private val wifiListAdapter by lazy { WifiListAdapter(this){ clickWifiItem(it) } }
    private val showNativeAd by lazy { ShowNativeAd(this,LocalConfig.SWAN_HOME_NA) }
    private val showFullAd by lazy { ShowFullAd(this,LocalConfig.SWAN_WIFICON_IN){ startConnectWifi() } }

    override fun layout(): Int = R.layout.activity_home

    override fun initEvent() = true

    override fun view() {
        immersionBar.statusBarView(top_view).init()
        setWifiListAdapter()
        setClickListener()
        sendReceiver()
        refresh_layout.setOnRefreshListener(this)
        scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            this.scrollY=scrollY
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        if(checkLocationPermission()){
            getCurrentWifi()
            scanWifiList()
        }else{
            if(refresh_layout.isRefreshing){
                refresh_layout.finishRefresh()
            }
        }
    }

    private fun setClickListener(){
        llc_wifi_scan.setOnClickListener {
            if(WifiUtils.getInstance(this)?.isWifiEnable == true){
                if (checkLocationPermission()){
                    startActivity(Intent(this,WifiSecurityPage::class.java))
                }
            }else{
                toast("Please open wifi")
            }
        }

        llc_net_test.setOnClickListener {
            if(WifiUtils.getInstance(this)?.isWifiEnable == true){
                if (checkLocationPermission()){
                    startActivity(Intent(this,NetTestPage::class.java))
                }
            }else{
                toast("Please open wifi")
            }
        }
        iv_history.setOnClickListener {
            startActivity(Intent(this,TestRecordPage::class.java))
        }
        iv_set.setOnClickListener {
            startActivity(Intent(this,SetPage::class.java))
        }
    }

    private fun setWifiListAdapter(){
        rv_wifi_list.apply {
            layoutManager=LinearLayoutManager(this@HomePage)
            adapter=wifiListAdapter
        }
    }

    private fun clickWifiItem(wifiInfoBean: WifiInfoBean){
        clickWifiInfoBean=wifiInfoBean
        if (wifiInfoBean.hasPwd){
            ConnectWifiDialog(wifiInfoBean){ pwd->
                wifiPwd=pwd
                showFullAd.showFull {
                    if (it){
                        startConnectWifi()
                    }
                }
            }.show(supportFragmentManager,"ConnectWifiDialog")
        }else{
            showFullAd.showFull {
                if (it){
                    startConnectWifi()
                }
            }
        }
    }

    private fun startConnectWifi(){
        clickWifiInfoBean?.let { wifiInfoBean->
            if (wifiInfoBean.hasPwd){
                CurrentWifiManager.currentWifiName=wifiInfoBean.name
                WifiUtils.getInstance(this)?.openWifi()
                WifiUtils.getInstance(this)?.connectWifiPws(wifiInfoBean.name,wifiPwd)
                MMKV.defaultMMKV().encode(wifiInfoBean.name,wifiPwd)

            }else{
                CurrentWifiManager.currentWifiName=wifiInfoBean.name
                WifiUtils.getInstance(this)?.connectWifiNoPws(wifiInfoBean.name)
            }
        }
    }

    private fun scanWifiList(){
        if(WifiUtils.getInstance(this)?.isWifiEnable == true){
            showHideWifiList(true)
            WifiUtils.getInstance(this)?.getWifiList()?.let { wifiListAdapter.updateList(it) }
            if(refresh_layout.isRefreshing){
                refresh_layout.finishRefresh()
            }
        }else{
            if(refresh_layout.isRefreshing){
                refresh_layout.finishRefresh()
            }
        }
    }


    private fun showHideWifiList(show:Boolean){
        rv_wifi_list.show(show)
    }

    private fun getCurrentWifi(noCheckConnect:Boolean=false){
        if(WifiUtils.getInstance(this)?.isConnectWifi() == true||noCheckConnect){
            llc_current_wifi.show(true)
            tv_current_wifi_connected.show(true)
            llc_connected_wifi.show(true)
            val wifiName = WifiUtils.getInstance(this)?.getCurrentWifiName()
            CurrentWifiManager.currentWifiName=wifiName?:""
            tv_current_wifi_name.text=wifiName
            tv_connected_wifi_name.text=wifiName
            scanWifiList()
        }else{
            hideCurrentWifi()
        }
    }

    private fun hideCurrentWifi(){
        llc_current_wifi.show(false)
        llc_connected_wifi.show(false)
        tv_current_wifi_connected.show(false)
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentWifi()
            }else{
                hideCurrentWifi()
            }
        }
    }

    private fun sendReceiver() {
        val filter = IntentFilter()
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
        wifiStateReceiver = WifiStateReceiver()
        registerReceiver(wifiStateReceiver, filter)
    }

    private fun stopReceiver() {
        if (wifiStateReceiver != null) {
            unregisterReceiver(wifiStateReceiver)
            wifiStateReceiver = null
        }
    }

    @Subscribe
    fun onEvent(eventBean: EventBean) {
        when(eventBean.code){
            EventCode.WIFI_OPEN->{
                if(checkLocationPermission()){
                    scanWifiList()
                }else{
                    showHideWifiList(false)
                    hideCurrentWifi()
                }
            }
            EventCode.WIFI_CLOSE->{
                hideCurrentWifi()
                showHideWifiList(false)
            }
            EventCode.WIFI_CONNECTED->{
                if(checkLocationPermission()){
                    getCurrentWifi(noCheckConnect = true)
                }else{
                    hideCurrentWifi()
                }
            }
            EventCode.WIFI_DISCONNECTED->{
                hideCurrentWifi()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(AdReloadManager.canReload(LocalConfig.SWAN_HOME_NA)&&scrollY<SizeUtils.dp2px(280F)){
            showNativeAd.startShow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showNativeAd.endShow()
        AdReloadManager.setBool(LocalConfig.SWAN_HOME_NA,true)
        stopReceiver()
    }
}