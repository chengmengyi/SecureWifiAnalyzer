package com.demo.securewifianalyzer.page.set

import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.base.BasePage
import com.demo.securewifianalyzer.config.LocalConfig
import kotlinx.android.synthetic.main.activity_url.*

class UrlPage:BasePage() {
    override fun layout(): Int = R.layout.activity_url

    override fun view() {
        immersionBar.statusBarView(top_view).statusBarDarkFont(false).init()
        iv_back.setOnClickListener { finish() }
        web_view.apply {
            settings.javaScriptEnabled=true
            loadUrl(LocalConfig.url)
        }
    }
}