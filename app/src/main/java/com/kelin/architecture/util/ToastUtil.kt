package com.kelin.architecture.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.kelin.architecture.R
import com.kelin.architecture.core.AppModule.getContext

/**
 * **描述: ** Toast的工具类。
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2018/3/30  上午10:34
 *
 * **版本: ** v 1.0.0
 */
object ToastUtil {
    /**
     * 用来获取Toast的文本内容的键。
     */
    private const val TOAST_CONTENT = "type"

    /**
     * 用来获取Toast的显示时间的键。
     */
    private const val TOAST_DURATION = "duration"

    /**
     * 用来获取Toast的布局资源的键。
     */
    private const val TOAST_LAYOUT = "toast_layout"

    /**
     * 用来获取Toast的图标的键。
     */
    private const val TOAST_ICON = "toast_icon"

    /**
     * Toast对象。
     */
    private var sToast: Toast? = null

    /**
     * Handler对象，用来处理子线程的Toast。
     */
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        @SuppressLint("ShowToast")
        override fun handleMessage(msg: Message) {
            val bundle = msg.data
            getToastInstance(
                bundle.getInt(TOAST_DURATION),
                bundle.getInt(TOAST_LAYOUT),
                bundle.getString(TOAST_CONTENT),
                bundle.getInt(TOAST_ICON)
            )!!.show()
        }
    }

    fun showLongToast(@StringRes text: Int) {
        showLongToast(context.getString(text))
    }

    fun showLongToast(text: String) {
        doOnShowToast(text, Toast.LENGTH_LONG, R.layout.toast_system_toast_layout, 0)
    }

    fun showShortToast(@StringRes text: Int) {
        showShortToast(context.getString(text))
    }

    fun showShortToast(text: CharSequence) {
        doOnShowToast(text, Toast.LENGTH_SHORT, R.layout.toast_system_toast_layout, 0)
    }

    fun showSuccessToast(@StringRes text: Int) {
        showSuccessToast(context.getString(text))
    }

    fun showLongSuccessToast(text: String) {
        doOnShowToast(text, Toast.LENGTH_LONG, R.layout.toast_custom_toast_layout, R.drawable.ic_toast_success)
    }

    fun showSuccessToast(text: String) {
        doOnShowToast(text, Toast.LENGTH_SHORT, R.layout.toast_custom_toast_layout, R.drawable.ic_toast_success)
    }

    fun showAlertToast(@StringRes text: Int) {
        showAlertToast(context.getString(text))
    }

    fun showAlertToast(text: String) {
        doOnShowToast(text, Toast.LENGTH_SHORT, R.layout.toast_custom_toast_layout, R.drawable.ic_toast_alert)
    }

    fun showErrorToast(@StringRes text: Int) {
        showErrorToast(context.getString(text))
    }

    fun showErrorToast(text: String) {
        doOnShowToast(text, Toast.LENGTH_SHORT, R.layout.toast_custom_toast_layout, R.drawable.ic_toast_alert)
    }

    fun showCustomToast(text: String, @DrawableRes icon: Int) {
        doOnShowToast(text, Toast.LENGTH_SHORT, R.layout.toast_custom_toast_layout, icon)
    }

    /**
     * 向Handle发送消息执行弹出Toast
     *
     * @param text        要提示的文本
     * @param duration    Toast的时长
     * @param toastLayout toast的布局。
     */
    private fun doOnShowToast(text: CharSequence, duration: Int, @LayoutRes toastLayout: Int, @DrawableRes icon: Int) {
        //如果是主线程直接showToast。
        if (TextUtils.equals("main", Thread.currentThread().name)) {
            getToastInstance(duration, toastLayout, text, icon)!!.show()
        } else { //如果是子线程则抛到主线程去showToast。
            val msg = Message.obtain()
            val bundle = Bundle()
            bundle.putString(TOAST_CONTENT, text.toString())
            bundle.putInt(TOAST_DURATION, duration)
            bundle.putInt(TOAST_LAYOUT, toastLayout)
            bundle.putInt(TOAST_ICON, toastLayout)
            msg.data = bundle
            mHandler.sendMessage(msg)
        }
    }

    @SuppressLint("ShowToast")
    private fun getToastInstance(duration: Int, toastLayout: Int, text: CharSequence?, @DrawableRes icon: Int): Toast? {
        //Android 7.1之后Toast的源码改变了，弹出过的Toast在一段时间内不能再使用(show方法无效)。
        if (sToast == null || Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            sToast = Toast.makeText(context, "", Toast.LENGTH_LONG)
        }
        var inflate: LayoutInflater? = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (inflate == null) {
            inflate = LayoutInflater.from(context)
        }
        sToast!!.view = inflate!!.inflate(toastLayout, null)
        val ivImage = sToast!!.view.findViewById<ImageView>(R.id.ivToastIcon)
        if (ivImage != null) {
            if (icon != 0) {
                ivImage.visibility = View.VISIBLE
                ivImage.setImageResource(icon)
            } else {
                ivImage.visibility = View.GONE
            }
            sToast!!.setGravity(Gravity.CENTER, 0, 0)
        } else {
            sToast!!.setGravity(
                Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
                0,
                context.resources.getDimensionPixelSize(R.dimen.toastYOffset)
            )
        }
        sToast!!.duration = duration
        sToast!!.setText(text)
        return sToast
    }

    private val context: Context
        get() = getContext().applicationContext

    /**
     * 取消Toast的显示。
     */
    fun cancel() {
        sToast!!.cancel()
    }
}