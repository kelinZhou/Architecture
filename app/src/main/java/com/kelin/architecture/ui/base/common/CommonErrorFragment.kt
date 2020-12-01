package com.kelin.architecture.ui.base.common

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kelin.architecture.R
import com.kelin.architecture.core.SystemError
import com.kelin.architecture.ui.base.BasicFragment
import kotlinx.android.synthetic.main.state_layout_common.view.*

/**
 * **描述:** 通用的错误页面。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/3/5  5:27 PM
 *
 * **版本:** v 1.0.0
 */
class CommonErrorFragment : BasicFragment() {

    companion object {

        private const val KEY_ERROR_MSG = "key_error_msg"

        fun createInstance(error: SystemError): CommonErrorFragment {
            return CommonErrorFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_ERROR_MSG, error.text)
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.state_layout_common, container, false)
        view.tvStatePageTitle.text = arguments?.getString(KEY_ERROR_MSG) ?: "系统错误"
        return view
    }
}