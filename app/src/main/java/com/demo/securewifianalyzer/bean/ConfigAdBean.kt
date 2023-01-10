package com.demo.securewifianalyzer.bean

class ConfigAdBean(
    val swan_s:String,
    val swan_l:Int,
    val swan_t:String,
    val swan_id:String,
) {
    override fun toString(): String {
        return "ConfigAdBean(swan_s='$swan_s', swan_l=$swan_l, swan_t='$swan_t', swan_id='$swan_id')"
    }
}