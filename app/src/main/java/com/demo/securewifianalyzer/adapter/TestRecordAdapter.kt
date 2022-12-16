package com.demo.securewifianalyzer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.bean.NetTestRecordBean
import kotlinx.android.synthetic.main.item_record.view.*
import java.text.SimpleDateFormat

class TestRecordAdapter(
    private val context: Context,
    private val list:ArrayList<NetTestRecordBean>
):RecyclerView.Adapter<TestRecordAdapter.RecordView>() {
    inner class RecordView(view:View):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordView {
        return RecordView(LayoutInflater.from(context).inflate(R.layout.item_record,parent,false))
    }

    override fun onBindViewHolder(holder: RecordView, position: Int) {
        with(holder.itemView){
            val bean = list[position]
            tv_item_wifi_name.text=bean.name
            tv_date.text=getDate(bean.time)
            tv_time.text=getTime(bean.time)
            tv_router.text= bean.router.toString()
            tv_tg.text= bean.tg.toString()
            tv_cn.text= bean.cn.toString()
        }
    }

    override fun getItemCount(): Int = list.size

    private fun getDate(time:Long):String{
        val simpleDateFormat = SimpleDateFormat("MM-dd")
        return simpleDateFormat.format(time)
    }

    private fun getTime(time:Long):String{
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        return simpleDateFormat.format(time)
    }
}