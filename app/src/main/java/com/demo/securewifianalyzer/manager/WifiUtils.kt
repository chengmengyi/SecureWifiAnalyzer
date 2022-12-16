package com.demo.securewifianalyzer.manager

import android.content.Context
import android.net.wifi.WifiManager
import java.util.ArrayList
import android.net.wifi.WifiConfiguration
import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import androidx.appcompat.app.AppCompatActivity
import com.demo.securewifianalyzer.bean.WifiInfoBean
import com.demo.securewifianalyzer.manager.WifiUtils
import java.lang.StringBuilder

class WifiUtils(private val context: Context) {
    private val wifiManager: WifiManager?

    companion object {
        private var utils: WifiUtils? = null
        fun getInstance(context: Context): WifiUtils? {
            if (utils == null) {
                synchronized(WifiUtils::class.java) {
                    if (utils == null) {
                        utils = WifiUtils(context)
                    }
                }
            }
            return utils
        }
    }

    init {
        wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    /**
     * wifi是否打开
     * @return
     */
    val isWifiEnable: Boolean
        get() {
            var isEnable = false
            if (wifiManager != null) {
                if (wifiManager.isWifiEnabled) {
                    isEnable = true
                }
            }
            return isEnable
        }

    /**
     * 打开WiFi
     */
    fun openWifi() {
        if (wifiManager != null && !isWifiEnable) {
            wifiManager.isWifiEnabled = true
        }
    }

    /**
     * 关闭WiFi
     */
    fun closeWifi() {
        if (wifiManager != null && isWifiEnable) {
            wifiManager.isWifiEnabled = false
        }
    }

    /**
     * 有密码连接
     * @param ssid
     * @param pws
     */
    fun connectWifiPws(ssid: String, pws: String) {
        wifiManager!!.disableNetwork(wifiManager.connectionInfo.networkId)
        val netId = wifiManager.addNetwork(getWifiConfig(ssid, pws, true))
        wifiManager.enableNetwork(netId, true)
    }

    /**
     * 无密码连接
     * @param ssid
     */
    fun connectWifiNoPws(ssid: String) {
        wifiManager!!.disableNetwork(wifiManager.connectionInfo.networkId)
        val netId = wifiManager.addNetwork(getWifiConfig(ssid, "", false))
        wifiManager.enableNetwork(netId, true)
    }

    /**
     * wifi设置
     * @param ssid
     * @param pws
     * @param isHasPws
     */
    private fun getWifiConfig(ssid: String, pws: String, isHasPws: Boolean): WifiConfiguration {
        val config = WifiConfiguration()
        config.allowedAuthAlgorithms.clear()
        config.allowedGroupCiphers.clear()
        config.allowedKeyManagement.clear()
        config.allowedPairwiseCiphers.clear()
        config.allowedProtocols.clear()
        config.SSID = "\"" + ssid + "\""
        val tempConfig = isExist(ssid)
        if (tempConfig != null) {
            wifiManager!!.removeNetwork(tempConfig.networkId)
        }
        if (isHasPws) {
            config.preSharedKey = "\"" + pws + "\""
            config.hiddenSSID = true
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            config.status = WifiConfiguration.Status.ENABLED
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        }
        return config
    }

    /**
     * 得到配置好的网络连接
     * @param ssid
     * @return
     */
    @SuppressLint("MissingPermission")
    private fun isExist(ssid: String): WifiConfiguration? {
        val configs = wifiManager!!.configuredNetworks
        for (config in configs) {
            if (config.SSID == "\"" + ssid + "\"") {
                return config
            }
        }
        return null
    }

    fun isConnectWifi(): Boolean {
        return if (wifiManager?.wifiState == WifiManager.WIFI_STATE_ENABLED) {
            val connManager = context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            wifiInfo!!.isConnected
        } else {
            false
        }
    }

    fun getCurrentWifiName():String {
        return try {
            val connectionInfo = wifiManager?.connectionInfo
            var ssid = connectionInfo?.ssid.toString()
            if (ssid.length>2&&ssid.startsWith('"')&&ssid.endsWith('"')){
                ssid=ssid.substring(1,ssid.length-1)
            }
            ssid
        }catch (e:Exception){
            ""
        }
    }

    fun getMaxSpeed():String{
        return try {
            val connectionInfo = wifiManager?.connectionInfo.toString()
            val speed = connectionInfo.substring(
                connectionInfo.indexOf("Max Supported Rx Link speed: "),
                connectionInfo.indexOf(", Frequency")
            )
            speed.replace("Max Supported Rx Link speed: ","")
        }catch (e:Exception){
            ""
        }
    }

    fun getWifiMac():String{
        val wifiInfo = wifiManager?.connectionInfo
        return wifiInfo?.bssid.toString()
    }

    fun getWifiIp(): String {
        return if (isWifiEnable) {
            val ipAsInt = wifiManager?.connectionInfo?.ipAddress
            if (null==ipAsInt || ipAsInt == 0) {
                ""
            } else {
                intToIp(ipAsInt)
            }
        } else {
            ""
        }
    }

    private fun intToIp(ipInt: Int): String {
        val sb = StringBuilder()
        sb.append(ipInt and 0xFF).append(".")
        sb.append(ipInt shr 8 and 0xFF).append(".")
        sb.append(ipInt shr 16 and 0xFF).append(".")
        sb.append(ipInt shr 24 and 0xFF)
        return sb.toString()
    }

    fun getWifiList(): MutableList<WifiInfoBean> {
        val list= arrayListOf<WifiInfoBean>()
        val scanResults = wifiManager?.scanResults
        scanResults?.forEach {
            if(!it.SSID.isNullOrEmpty()&&it.SSID!=CurrentWifiManager.currentWifiName){
                list.add(
                    WifiInfoBean(
                        name = it.SSID,
                        hasPwd = checkWifiHasPwd(it),
                        level = it.level
                    )
                )
            }
        }
        return list.sortedByDescending { it.level }.toMutableList()
    }

    private fun checkWifiHasPwd(scanResult: ScanResult):Boolean{
        try {
            if(null!=scanResult.capabilities){
                val capabilities = scanResult.capabilities.trim()
                if(capabilities==""||capabilities=="[ESS]"){
                    return false
                }
            }
        }catch (e:Exception){
            return true
        }
        return true
    }
}