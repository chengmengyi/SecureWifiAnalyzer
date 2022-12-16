package com.demo.securewifianalyzer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.app.show
import com.demo.securewifianalyzer.bean.WifiInfoBean
import kotlinx.android.synthetic.main.item_wifi_list.view.*

class WifiListAdapter(
    private val context: Context,
    private val click:(bean:WifiInfoBean)->Unit
):RecyclerView.Adapter<WifiListAdapter.WifiView>() {
    private var list= mutableListOf<WifiInfoBean>()

    fun updateList(list:MutableList<WifiInfoBean>){
        this.list=list
        notifyDataSetChanged()
    }

    inner class WifiView(view:View):RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener { click.invoke(list[layoutPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiView {
        return WifiView(LayoutInflater.from(context).inflate(R.layout.item_wifi_list,parent,false))
    }

    override fun onBindViewHolder(holder: WifiView, position: Int) {
        with(holder.itemView){
            val wifiInfoBean = list[position]
            iv_item_suo.show(wifiInfoBean.hasPwd)
            tv_item_wifi_name.text=wifiInfoBean.name
            iv_item_wifi_icon.setImageResource(wifiInfoBean.getLevelIcon())
        }
    }

    override fun getItemCount(): Int = list.size
}