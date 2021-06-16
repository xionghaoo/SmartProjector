package com.ubtrobot.smartprojector.ui

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.ui.call.BaseCallActivity

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = AlertDialog.Builder(this, R.style.TransparentDialog)
            .setView(layoutInflater.inflate(R.layout.dialog_loading_view, null))
            .setCancelable(false)
            .create()
    }
}