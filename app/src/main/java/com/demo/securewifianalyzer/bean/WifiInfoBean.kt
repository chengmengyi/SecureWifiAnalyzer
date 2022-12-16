package com.demo.securewifianalyzer.bean

import com.demo.securewifianalyzer.R

class WifiInfoBean(
    val name:String,
    val hasPwd:Boolean,
    val level:Int,
) {

    fun getLevelIcon() = when {
        level>=-25 -> {
            R.drawable.wifi4
        }
        level>=-50 -> {
            R.drawable.wifi3
        }
        level>=-75 -> {
            R.drawable.wifi2
        }
        else -> {
            R.drawable.wifi1
        }
    }

}