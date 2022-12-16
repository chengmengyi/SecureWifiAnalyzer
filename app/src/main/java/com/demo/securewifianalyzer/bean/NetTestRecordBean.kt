package com.demo.securewifianalyzer.bean

import org.litepal.crud.LitePalSupport

data class NetTestRecordBean(
    val name:String,
    val time:Long,
    val router:Int,
    val tg:Int,
    val cn:Int,
): LitePalSupport()