package com.demo.securewifianalyzer.page.set

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.app.toast
import com.demo.securewifianalyzer.base.BasePage
import com.demo.securewifianalyzer.config.LocalConfig
import kotlinx.android.synthetic.main.activity_set.*
import java.lang.Exception

class SetPage:BasePage() {
    override fun layout(): Int = R.layout.activity_set

    override fun view() {
        immersionBar.statusBarView(top_view).statusBarDarkFont(false).init()
        iv_back.setOnClickListener { finish() }
        llc_contact.setOnClickListener {
            try {
                val uri = Uri.parse("mailto:${LocalConfig.email}")
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                startActivity(intent)
            }catch (e: Exception){
                toast("Contact us by email：${LocalConfig.email}")
            }
        }
        llc_agree.setOnClickListener { startActivity(Intent(this,UrlPage::class.java)) }
        llc_update.setOnClickListener {
            val pm = packageManager
            val packageName=pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).packageName
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=${packageName}"
            )
            startActivity(Intent.createChooser(intent, "share"))
        }
        llc_share.setOnClickListener {
            val packName = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).packageName
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=$packName")
            }
            startActivity(intent)
        }
    }
}