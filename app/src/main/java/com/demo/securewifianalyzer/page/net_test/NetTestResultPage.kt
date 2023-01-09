package com.demo.securewifianalyzer.page.net_test

import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.base.BasePage
import com.demo.securewifianalyzer.bean.NetTestRecordBean
import com.demo.securewifianalyzer.manager.TestRecordManager
import kotlinx.android.synthetic.main.activity_net_test_result.*
import kotlinx.android.synthetic.main.layout_net_delay.*
import kotlinx.android.synthetic.main.layout_net_level.*

class NetTestResultPage:BasePage() {

    override fun layout(): Int = R.layout.activity_net_test_result

    override fun view() {
        immersionBar.statusBarView(top_view).init()
        setTestInfo()
        iv_back.setOnClickListener { finish() }
    }

    private fun setTestInfo(){
        val googleDelay = intent.getIntExtra("googleDelay", 0)
        val twitterDelay = intent.getIntExtra("twitterDelay", 0)
        val facebookDelay = intent.getIntExtra("facebookDelay", 0)
        tv_delay1.text="${googleDelay}ms"
        tv_delay2.text="${twitterDelay}ms"
        tv_delay3.text="${facebookDelay}ms"
        val speed = intent.getLongExtra("speed", 0L)/1024
        val level = getLevel(speed)
        tv_speed_tips.text="According to your speed，We recommend $level videos."
        setLevelIcon(level)

        TestRecordManager.saveTestRecord(
            NetTestRecordBean(
                name = intent.getStringExtra("wifiName")?:"",
                time = System.currentTimeMillis(),
                router = googleDelay,
                tg = twitterDelay,
                cn = facebookDelay
            )
        )
    }

    private fun setLevelIcon(level:String){
        when(level){
            "360P"->{
                iv_360.setImageResource(R.drawable.net_test5)
            }
            "720P"->{
                iv_360.setImageResource(R.drawable.net_test4)
                iv_720.setImageResource(R.drawable.net_test5)
            }
            "1080P"->{
                iv_360.setImageResource(R.drawable.net_test4)
                iv_720.setImageResource(R.drawable.net_test4)
                iv_1080.setImageResource(R.drawable.net_test5)
            }
            "4K"->{
                iv_360.setImageResource(R.drawable.net_test4)
                iv_720.setImageResource(R.drawable.net_test4)
                iv_1080.setImageResource(R.drawable.net_test4)
                iv_4k.setImageResource(R.drawable.net_test5)
            }
        }
    }

    private fun getLevel(speed:Long):String{
        if(speed<=0){
            return "0P"
        }
        return when(speed){
            //720p
            in 100..400->"720P"
            //1080p
            in 400..900->"1080P"
            //4K
            in 900..Int.MAX_VALUE->"4K"
            //360P
            else->"360P"
        }
    }
}