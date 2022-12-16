package com.demo.securewifianalyzer.manager

import android.util.Log
import com.demo.securewifianalyzer.bean.NetTestRecordBean
import org.litepal.LitePal
import java.lang.Exception

object TestRecordManager {
    fun saveTestRecord(netTestRecordBean: NetTestRecordBean){
        try {
            val save = netTestRecordBean.save()
        }catch (e:Exception){

        }
    }

    fun queryTestRecord(offset:Int):List<NetTestRecordBean>{
        return LitePal.select("*")
            .order("time desc")
            .limit(20)
            .offset(offset)
            .find(NetTestRecordBean::class.java)
    }
}