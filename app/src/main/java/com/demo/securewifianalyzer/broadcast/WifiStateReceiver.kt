package com.demo.securewifianalyzer.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.NetworkInfo.DetailedState
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import com.demo.securewifianalyzer.eventbus.EventBean
import com.demo.securewifianalyzer.eventbus.EventCode
import android.net.wifi.WifiInfo
import com.demo.securewifianalyzer.app.toast
import com.demo.securewifianalyzer.manager.ActivityCallback
import com.demo.securewifianalyzer.manager.CurrentWifiManager
import com.tencent.mmkv.MMKV

class WifiStateReceiver: BroadcastReceiver()  {

    override fun onReceive(context: Context, intent: Intent) {
        // 这个监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION == intent.action) {
            when (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)) {
                WifiManager.WIFI_STATE_DISABLED -> {
                    EventBean(EventCode.WIFI_CLOSE).send()
                }
                WifiManager.WIFI_STATE_DISABLING -> {}
                WifiManager.WIFI_STATE_ENABLED -> {
                    EventBean(EventCode.WIFI_OPEN).send()
                }
                WifiManager.WIFI_STATE_ENABLING -> {}
                WifiManager.WIFI_STATE_UNKNOWN -> {}
            }
        }
        // 这个监听wifi的连接状态即是否连上了一个有效无线路由，
        // 当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
        // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
        // 当然刚打开wifi肯定还没有连接到有效的无线
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION == intent.action) {
//            Log.i("tag", "NETWORK_STATE_CHANGED_ACTION")
            val parcelableExtra = intent
                .getParcelableExtra<Parcelable>(WifiManager.EXTRA_NETWORK_INFO)
            if (null != parcelableExtra) {
                val networkInfo = parcelableExtra as NetworkInfo
                val state = networkInfo.state
                val isConnected = state == NetworkInfo.State.CONNECTED // 当然，这边可以更精确的确定状态
//                Log.i("tag", "NETWORK_STATE_CHANGED_ACTION=====${isConnected}")
                if (isConnected) {
                    EventBean(EventCode.WIFI_CONNECTED).send()
                } else {
                    EventBean(EventCode.WIFI_DISCONNECTED).send()
                }
            }
        }
        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION == intent.action) {
            handleSupplicantState(intent,context)
        }

        // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
        // 最好用的还是这个监听。
        // wifi如果打开，关闭，以及连接上可用的连接都会接到监听。
        // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
//            if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
//                Log.i("tag", "CONNECTIVITY_ACTION")
//                val manager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//                val gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//                val wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//                Log.i("tag", "网络状态改变:" + wifi!!.isConnected + " 3g:" + gprs!!.isConnected)
//                val info =
//                    intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)
//                if (info != null) {
//                    Log.i("tag", "info.getTypeName():" + info.typeName)
//                    Log.i("tag", "getSubtypeName():" + info.subtypeName)
//                    Log.i("tag", "getState():" + info.state)
//                    Log.i("tag", "getDetailedState():" + info.detailedState.name)
//                    Log.i("tag", "getDetailedState():" + info.extraInfo)
//                    Log.i("tag", "getType():" + info.type)
//                    if (NetworkInfo.State.CONNECTED == info.state) {
//                    } else if (info.type == 1) {
//                        if (NetworkInfo.State.DISCONNECTING == info.state) {
//                        }
//                    }
//                }
//            }
//            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION == intent.action) {
//                Log.i("tag", "SUPPLICANT_STATE_CHANGED_ACTION")
//                //                WifiManager.WIFI_STATE_DISABLED ==1
////                WifiManager.WIFI_STATE_DISABLING ==0
////                WifiManager. WIFI_STATE_ENABLED==3
////                WifiManager. WIFI_STATE_ENABLING==2
////                WifiManager. WIFI_STATE_UNKNOWN==4
//                val linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1)
//                Log.i("tag", "linkWifiResult -> $linkWifiResult")
//            }
    }

    @SuppressLint("MissingPermission")
    private fun handleSupplicantState(intent: Intent, context: Context) {
        val error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1)
        if (error == WifiManager.ERROR_AUTHENTICATING) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val configurations = wifiManager.configuredNetworks
            for (configuration in configurations) {
                val replace = configuration.SSID.replace("\"", "")
                if (replace == CurrentWifiManager.currentWifiName) {
                    if(ActivityCallback.isFront){
                        context.toast("密码错误")
                    }
                    var removeResult = wifiManager.removeNetwork(configuration.networkId)
                    removeResult = removeResult and wifiManager.saveConfiguration()
                    CurrentWifiManager.currentWifiName=""
                    MMKV.defaultMMKV().encode(replace,"")
                }
            }
        }
    }
}