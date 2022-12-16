package com.demo.securewifianalyzer.eventbus

import org.greenrobot.eventbus.EventBus

class EventBean(
    val code:Int
) {
    fun send(){
        EventBus.getDefault().post(this)
    }
}