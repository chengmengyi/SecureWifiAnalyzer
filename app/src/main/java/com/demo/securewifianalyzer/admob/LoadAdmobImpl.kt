package com.demo.securewifianalyzer.admob

import com.demo.securewifianalyzer.app.log
import com.demo.securewifianalyzer.bean.ConfigAdBean
import com.demo.securewifianalyzer.bean.ResultAdBean
import com.demo.securewifianalyzer.config.LocalConfig

object LoadAdmobImpl:LoadAdmob() {
    var showingFull=false
    private val loading= arrayListOf<String>()
    private val resultAdMap= hashMapOf<String,ResultAdBean>()

    fun load(type:String,loadOpenAgain:Boolean=true){
        if (loading.contains(type)){
            "$type ad is loading".log()
            return
        }
        if (resultAdMap.containsKey(type)){
            val resultAdBean = resultAdMap[type]
            if (null!=resultAdBean?.ad){
                if (resultAdBean.expired()){
                    removeAdByType(type)
                }else{
                    "$type ad has cache".log()
                    return
                }
            }
        }
        val parseAdList = parseAdList(type)
        if (parseAdList.isEmpty()){
            "$type ad list is empty".log()
            return
        }
        loading.add(type)
        loadAd(type,parseAdList.iterator(),loadOpenAgain)
    }

    private fun loadAd(key: String, iterator: Iterator<ConfigAdBean>, loadOpenAgain: Boolean){
        loadAdmob(key,iterator.next()){
            if(it.fail.isEmpty()){
                "$key load success".log()
                loading.remove(key)
                resultAdMap[key]=it
            }else{
                "$key load fail, ${it.fail}".log()
                if(iterator.hasNext()){
                    loadAd(key,iterator,loadOpenAgain)
                }else{
                    loading.remove(key)
                    if(loadOpenAgain&&key==LocalConfig.SWAN_START){
                        load(key,loadOpenAgain = false)
                    }
                }
            }
        }
    }

    fun getAdByType(type: String)= resultAdMap[type]?.ad

    fun removeAdByType(type: String){
        resultAdMap.remove(type)
    }
}