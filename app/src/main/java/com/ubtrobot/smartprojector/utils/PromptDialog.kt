package com.ubtrobot.smartprojector.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes

typealias ViewConfigCallback = (v: View) -> Unit
typealias OperationCallback = (v: View) -> Unit

class PromptDialog private constructor(
    private val context: Context,
    private val theme: Int,
    @LayoutRes
    private val layoutId: Int,
    private var callback: ViewConfigCallback?,
    private var operations: HashSet<Operation> = HashSet()
){

    private var dialog: AlertDialog

    init {
        val contentView = LayoutInflater.from(context).inflate(layoutId, null)
        callback?.invoke(contentView)
        dialog = AlertDialog.Builder(context, theme)
            .setView(contentView)
            .create()

        operations.forEach { operation ->
            when (operation.type) {
                OperationType.CONFIRM, OperationType.CANCEL -> {
                    contentView.findViewById<View>(operation.viewId).setOnClickListener {
                        operation.operation?.invoke(contentView)
                        if (operation.autoDismiss) {
                            dialog.dismiss()
                        }
                    }
                }
            }
        }
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    class Builder(
        private val context: Context,
        private val theme: Int = 0
    ) {

        private var layoutId: Int? = null
        private var callback: ViewConfigCallback? = null
        private var operations: HashSet<Operation> = HashSet()

        fun setView(@LayoutRes layoutId: Int) : Builder {
            this.layoutId = layoutId
            return this
        }

        fun addOperation(
            type: OperationType,
            viewId: Int,
            autoDismiss: Boolean = true,
            operation: OperationCallback?
        ) : Builder {
            operations.add(Operation(type, viewId, autoDismiss, operation))
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
            return PromptDialog(
                context = context,
                    theme = theme,
                layoutId = layoutId!!,
                callback = callback,
                operations = operations
            )
        }
    }

}

enum class OperationType {
    CONFIRM, CANCEL
}

class Operation(
    val type: OperationType,
    val viewId: Int,
    val autoDismiss: Boolean = true,
    val operation: OperationCallback?
) {

    override fun hashCode(): Int {
        return type.hashCode()
    }

    override fun equals(other: Any?): Boolean =
        if (other is Operation) type == other.type else false
}