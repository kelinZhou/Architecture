package com.kelin.architecture.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.kelin.architecture.R
import java.util.*

/**
 * **描述:** 所有的弹窗统一在这里处理，便于维护。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-09 22:48
 *
 * **版本:** v 1.0.0
 */
object StyleHelper {

    private val sDialogMap = HashMap<Context, Dialog>()

    fun showProgress(context: Context?, cancelable: Boolean = true, text: String = "请稍候...") {
        if (context == null) {
            return
        }
        var dialog = sDialogMap[context]
        if (dialog == null) {
            dialog = Dialog(context, R.style.CommonWidgetDialog_Center)
            dialog.setContentView(R.layout.loading_progress)
            val params = dialog.window!!.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes = params
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(cancelable)
            dialog.setOnDismissListener { sDialogMap.remove(context) }
            dialog.show()
            sDialogMap[context] = dialog
        }

        val tv = dialog.findViewById<TextView>(R.id.tv_load_dialog)
        tv.visibility = if (text.isEmpty()) {
            View.GONE
        } else {
            tv.text = text
            View.VISIBLE
        }
    }

    fun hideProgress(context: Context?) {
        if (context == null) {
            return
        }
        val dialog = sDialogMap[context]
        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
            sDialogMap.remove(context)
        }
    }

    fun showOperationDialog(activity: Activity, title: String? = null, vararg operations: String, onClick: (operation: String) -> Unit) {
        if (operations.isNotEmpty()) {
            val dialog = DialogHelper.Builder(activity)
                .setContentView(R.layout.dialog_mutil_operation)
                .setCancelable(true)
                .setStyle(R.style.CommonWidgetDialog_BottomAnim)
                .setSize(-1, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.BOTTOM)
                .createDialog()
            val llOperations = dialog.findViewById<ViewGroup>(R.id.llOperations)
            if (title?.isNotEmpty() == true) {
                dialog.findViewById<View>(R.id.llTitle).visibility = View.VISIBLE
                dialog.findViewById<TextView>(R.id.tvTitle).text = title
            }
            val inflater = LayoutInflater.from(activity)
            operations.forEach { operation ->
                inflater.inflate(R.layout.layout_operation_dialog_item, llOperations, false).apply {
                    findViewById<TextView>(R.id.btnOperation).apply {
                        text = operation
                        setOnClickListener {
                            dialog.dismiss()
                            onClick(operation)
                        }
                    }
                    llOperations.addView(this)
                }
            }
            dialog.findViewById<View>(R.id.btnCancel).setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }


//    @SuppressLint("SetTextI18n")
//    fun showEnvSwitcherDialog(context: Activity, onSwitch: (envType: EnvConfig.Type) -> Unit) {
//        val dialog = DialogHelper.Builder(context)
//            .setContentView(R.layout.dialog_env_switcher)
//            .setCancelable(true).setStyle(R.style.CommonWidgetDialog_Center)
//            .setSize(DeviceUtil.getScreenWidth(context, 0.5), -2)
//            .setGravity(Gravity.CENTER).createDialog()
//
//
//        val buttons = listOf<Button>(
//            dialog.findViewById(R.id.dialog_dev),
//            dialog.findViewById(R.id.dialog_test),
//            dialog.findViewById(R.id.dialog_release),
//            dialog.findViewById(R.id.dialog_prepare)
//        )
//
//        EnvConfig.Type.values().forEachIndexed { index, type ->
//            val button = buttons[index]
//            button.visibility = View.VISIBLE
//            button.text = "${type.alias}环境"
//            button.setOnClickListener {
//                dialog.dismiss()
//                onSwitch(type)
//            }
//        }
//        dialog.show()
//    }

    fun showCommonDialog(
        context: Activity,
        title: CharSequence?,
        msg: CharSequence,
        btnNegativeText: String? = "取消",
        btnPositiveText: String = "确定",
        cancelable: Boolean = false,
        dismissListener: ((isSure: Boolean) -> Unit)? = null
    ) {
        val dialog = DialogHelper.Builder(context)
            .setContentView(R.layout.dialog_error_hint)
            .setStyle(R.style.CommonWidgetDialog)
            .setCancelable(cancelable)
            .setSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            .setGravity(Gravity.CENTER)
            .createDialog()
        val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
        val tvMsg = dialog.findViewById<TextView>(R.id.tvMsg)
        if (title != null && title.isNotEmpty()) {
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = title
        } else {
            tvMsg.setTextColor(context.resources.getColor(R.color.grey_333))
        }
        tvMsg.text = msg

        if (TextUtils.isEmpty(msg)) {
            tvMsg.visibility = View.GONE
        }

        val btnNegative = dialog.findViewById<Button>(R.id.btnNegative)
        val btnPositive = dialog.findViewById<Button>(R.id.btnPositive)
        if (btnNegativeText == null || btnNegativeText.isEmpty()) {
            btnNegative.visibility = View.GONE
            dialog.findViewById<View>(R.id.tvBtnSlicer).visibility = View.GONE
            btnPositive.setBackgroundResource(R.drawable.selector_recycler_item_bg_10_bl_br)
        } else {
            btnNegative.text = btnNegativeText
        }
        btnPositive.text = btnPositiveText
        btnNegative.setOnClickListener {
            dismissListener?.invoke(false)
            dialog.dismiss()
        }
        btnPositive.setOnClickListener {
            dismissListener?.invoke(true)
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showMissCameraDialog(activity: Activity, renewable: (isContinue: Boolean) -> Unit) {
        AlertDialog.Builder(activity)
            .setCancelable(false)
            .setTitle("提示")
            .setMessage("使用相机前，应用需要获取相机的使用权限")
            .setNegativeButton("取消") { _, _ ->
                renewable(false)
            }
            .setPositiveButton("去设置") { _, _ ->
                renewable(true)
            }.show()
    }

    fun showMissStorageDialog(activity: Activity, msg: String, renewable: (isContinue: Boolean) -> Unit) {
        AlertDialog.Builder(activity)
            .setCancelable(false)
            .setTitle("提示")
            .setMessage(msg)
            .setNegativeButton("取消") { _, _ ->
                renewable(false)
            }
            .setPositiveButton("去设置") { _, _ ->
                renewable(true)
            }.show()
    }
}