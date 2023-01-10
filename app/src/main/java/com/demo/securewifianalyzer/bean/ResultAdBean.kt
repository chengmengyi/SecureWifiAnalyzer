package com.demo.securewifianalyzer.bean

class ResultAdBean(
    val ad:Any?=null,
    val time:Long=0L,
    var fail:String="",
) {

    fun expired()=(System.currentTimeMillis() - time) >=3600000L
}