package com.demo.securewifianalyzer.app

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.math.roundToInt
import android.location.LocationManager

import android.content.Context.LOCATION_SERVICE
import java.util.*

fun String.log(){
    Log.e("qwer",this)
}

fun View.show(show:Boolean){
    visibility=if (show) View.VISIBLE else View.GONE
}

fun Context.toast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_LONG).show()
}

fun hasOverlayPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return true
    return Settings.canDrawOverlays(context)
}

fun Context.isLocationProviderEnabled(): Boolean {
    var result = false
    val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager ?: return false
    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager
            .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    ) {
        result = true
    }
    return result
}


fun byteToMB(size: Long): String {
    val kb: Long = 1024
    val mb = kb * 1024
    val gb = mb * 1024
    return when {
        size >= gb -> {
            String.format("%.1f GB", size.toFloat() / gb)
        }
        size >= mb -> {
            val f = size.toFloat() / mb
            String.format(if (f > 100) "%.0f MB" else "%.1f MB", f)
        }
        size > kb -> {
            val f = size.toFloat() / kb
            String.format(if (f > 100) "%.0f KB" else "%.1f KB", f)
        }
        else -> {
            String.format("%d B", size)
        }
    }
}

suspend fun getDelay(ip: String): Int {
    var delay = Random().nextInt(200)
    var timeout = 1
    val cmd = "/system/bin/ping -w $timeout $ip"
    return withContext(Dispatchers.IO) {
        val r = ping(cmd)
        if (r != null) {
            try {
                val index: Int = r.indexOf("min/avg/max/mdev")
                if (index != -1) {
                    val tempInfo: String = r.substring(index + 19)
                    val temps = tempInfo.split("/".toRegex()).toTypedArray()
                    delay = temps[0].toFloat().roundToInt()
                    if(delay<=0){
                        delay = Random().nextInt(200)
                    }
                }
            } catch (e: Exception) {

            }
        }
        delay
    }
}

private fun ping(cmd: String): String? {
    var process: Process? = null
    try {
        process = Runtime.getRuntime().exec(cmd) //执行ping指令
        val inputStream = process!!.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))
        val sb = StringBuilder()
        var line: String?
        while (null != reader.readLine().also { line = it }) {
            sb.append(line)
            sb.append("\n")
        }
        reader.close()
        inputStream.close()
        return sb.toString()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        process?.destroy()
    }
    return null
}