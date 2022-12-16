package com.demo.securewifianalyzer.page.net_test

import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.adapter.TestRecordAdapter
import com.demo.securewifianalyzer.base.BasePage
import com.demo.securewifianalyzer.bean.NetTestRecordBean
import com.demo.securewifianalyzer.manager.TestRecordManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.activity_test_record.*

class TestRecordPage:BasePage(), OnRefreshLoadMoreListener {
    private var offset=0
    private val recordList= arrayListOf<NetTestRecordBean>()
    private val recordAdapter by lazy { TestRecordAdapter(this,recordList) }

    override fun layout(): Int = R.layout.activity_test_record

    override fun view() {
        immersionBar.statusBarView(top_view).statusBarDarkFont(false).init()
        setAdapter()
        iv_back.setOnClickListener { finish() }
        refresh_layout.setOnRefreshLoadMoreListener(this)
        refresh_layout.autoRefresh()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        offset=0
        queryRecord()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        queryRecord()
    }

    private fun queryRecord(){
        val queryTestRecord = TestRecordManager.queryTestRecord(offset)
        if(queryTestRecord.isNotEmpty()){
            if (offset==0){
                recordList.clear()
            }
            offset+=queryTestRecord.size
            recordList.addAll(queryTestRecord)
            recordAdapter.notifyDataSetChanged()
        }
        if (refresh_layout.isRefreshing){
            refresh_layout.finishRefresh()
        }
        if (refresh_layout.isLoading){
            refresh_layout.finishLoadMore()
        }
    }

    private fun setAdapter(){
        rv_record.apply {
            layoutManager=LinearLayoutManager(this@TestRecordPage)
            adapter=recordAdapter
        }
    }
}