package com.demo.securewifianalyzer.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tencent.mmkv.MMKV

object FireConfig {

    fun readFire(){
//        val remoteConfig = Firebase.remoteConfig
//        remoteConfig.fetchAndActivate().addOnCompleteListener {
//            if (it.isSuccessful){
//                writeAdStr(remoteConfig.getString("swan_adve"))
//            }
//        }
    }

    private fun writeAdStr(string: String){
        MMKV.defaultMMKV().encode("swan_adve",string)
    }

    fun getAdStr():String{
        val decodeString = MMKV.defaultMMKV().decodeString("swan_adve", "")?:""
        if (decodeString.isEmpty()) return LocalConfig.LOCAL_AD_STR
        return decodeString
    }
}