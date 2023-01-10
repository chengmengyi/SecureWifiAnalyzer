package com.demo.securewifianalyzer.dialog

import android.content.Intent
import android.provider.Settings
import android.view.View
import com.demo.securewifianalyzer.R
import com.demo.securewifianalyzer.app.isLocationProviderEnabled
import com.demo.securewifianalyzer.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_location.*

class LocationDialog(private val callback:(has:Boolean)->Unit):BaseDialog() {

    override fun layout(): Int = R.layout.dialog_location

    override fun view(v:View) {
        dialog?.setCancelable(false)
        tv_sure.setOnClickListener {
            val i = Intent()
            i.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            startActivityForResult(i, 1000)
        }

        tv_cancel.setOnClickListener {
            dismiss()
            callback.invoke(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1000){
            if(requireContext().isLocationProviderEnabled()){
                dismiss()
                callback.invoke(true)
            }
        }
    }
}