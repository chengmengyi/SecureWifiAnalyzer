package com.demo.securewifianalyzer.admob

object AdReloadManager {
    private val map= hashMapOf<String,Boolean>()

    fun canReload(key:String)=map[key]?:true

    fun setBool(key: String,boolean: Boolean){
        map[key]=boolean
    }

    fun resetAll(){
        map.keys.forEach {
            map[it]=true
        }
    }
}