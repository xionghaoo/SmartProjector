package com.ubtrobot.smartprojector.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.ubtrobot.smartprojector.R

typealias ViewConfigCallback = (v: View) -> Unit

class PromptDialog private constructor(
    private val context: Context,
    @LayoutRes
    private val layoutId: Int,
    private var callback: ViewConfigCallback?
){

    private var dialog: AlertDialog

    init {
        val contentView = LayoutInflater.from(context).inflate(layoutId, null)
        callback?.invoke(contentView)
        dialog = AlertDialog.Builder(context)
            .setView(contentView)
            .create()

        contentView.findViewById<View>(R.id.btn_confirm).setOnClickListener {
            dialog.dismiss()
        }
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    class Builder(
        private val context: Context
    ) {

        private var layoutId: Int? = null
        private var callback: ViewConfigCallback? = null

        fun setView(@LayoutRes layoutId: Int) : Builder {
            this.layoutId = layoutId
            return this
        }

        fun configView(callback: ViewConfigCallback?) : Builder {
            this.callback = callback
            return this
        }

        fun build() : PromptDialog {
            if (layoutId == null) {
                throw IllegalStateException("layout id is null")
            }
            return PromptDialog(context, layoutId!!, callback)
        }
    }

}