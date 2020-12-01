package com.kelin.architecture.util

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

class DialogHelper(
    context: Activity,
    style: Int,
    cancelListener: ((dialog: DialogInterface) -> Unit)? = null
) : Dialog(context, style) {

    init {
        if (cancelListener != null) {
            setOnCancelListener(cancelListener)
        }
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: Exception) {
        }
    }

    class Builder(private val context: Activity) {
        private var contentView: View? = null
        private var contentLayout = 0
        private var contentParams: ViewGroup.LayoutParams? = null
        private var style = 0

        // Window默认就是MATCH_PARENT, 但是我们是dialog，一般不需要全屏，所以可以设置为WRAP_CONTENT
        private var width = WindowManager.LayoutParams.WRAP_CONTENT // WindowManager.LayoutParams.MATCH_PARENT;
        private var height = WindowManager.LayoutParams.WRAP_CONTENT
        private var gravity = 0
        private var isCancelable = false
        private var x = 0
        private var y = 0

        fun setContentView(view: View?): Builder {
            contentView = view
            return this
        }

        fun setContentView(view: View?, w: Int, h: Int): Builder {
            contentView = view
            contentParams = ViewGroup.LayoutParams(w, h)
            return this
        }

        fun setContentView(layout: Int): Builder {
            contentLayout = layout
            return this
        }

        fun setStyle(style: Int): Builder {
            this.style = style
            return this
        }

        fun setPosition(x: Int, y: Int): Builder {
            this.x = x
            this.y = y
            return this
        }

        fun setSize(width: Int, height: Int): Builder {
            this.width = width
            this.height = height
            return this
        }

        fun setGravity(gravity: Int): Builder {
            this.gravity = gravity
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            isCancelable = cancelable
            return this
        }

        fun createDialog(): Dialog {
            return createDialog(context, contentLayout, contentView, contentParams)
        }

        fun createDialog(context: Activity, contentView: View?): Dialog {
            return createDialog(
                context,
                0,
                contentView,
                contentParams,
                width,
                height,
                isCancelable,
                style
            )
        }

        @JvmOverloads
        fun createDialog(
            context: Activity,
            contentViewLayout: Int,
            contentView: View?,
            contentLayoutParams: ViewGroup.LayoutParams?,
            width: Int = this.width,
            height: Int = this.height,
            cancellable: Boolean = isCancelable,
            style: Int = this.style
        ): Dialog {
            val dialog: Dialog = DialogHelper(context, style)
            // requestFeature() must be called before adding content
            // 设置requestFeature要放在setContentView之前
            // dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(cancellable)
            dialog.setCancelable(cancellable)
            if (contentView != null) {
                if (contentLayoutParams != null) {
                    dialog.setContentView(contentView, contentLayoutParams)
                } else {
                    dialog.setContentView(contentView)
                }
            } else if (contentViewLayout != 0) {
                dialog.setContentView(contentViewLayout)
            }
            dialog.window?.apply {
                val params = attributes?.also {

                    it.width = width
                    // 设置dialog中decor的宽和高，注意需要放在setContentView之后
                    // 因为dialog默认WRAP_CONTENT，如果在setContentView之前设置，就会被覆盖成WRAP_CONTENT
                    it.height = height

                    if (x != 0) {
                        it.x = x
                    }
                    if (y != 0) {
                        it.y = y
                    }
                }
                attributes = params
                setGravity(if (gravity == 0) Gravity.CENTER else gravity)
            }
            return dialog
        }
    }
}