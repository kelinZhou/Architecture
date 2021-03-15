package com.kelin.architecture.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.kelin.architecture.R
import com.kelin.architecture.tools.FinishHelper
import com.kelin.architecture.ui.base.BasicFragment
import kotlinx.android.synthetic.main.fragment_guide_page.view.*

/**
 * **描述: ** 引导页面。
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2020/12/11  下午9:26
 *
 * **版本: ** v 1.0.0
 */
class GuideFragment : BasicFragment() {

    private val finishHelper = FinishHelper.createInstance()
    private lateinit var nextAction: SplashHelper.NextAction
    private lateinit var rootView: View

    private fun onOpenTheApp() {
        GuideHelper.setNotFirstOpen(context)
        SplashHelper.handlerNextAction(requireActivity(), nextAction)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nextAction = GuideHelper.getNextAction(arguments)
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_guide_page, container, false)
        rootView.btnOpen.setOnClickListener { onOpenTheApp() }
        rootView.alpha = 0F
//        rootView.bvGuide.setEntries(guideEntries, false)
        return rootView
    }


    override fun onResume() {
        super.onResume()
        ViewCompat.animate(rootView)
            .alpha(1F)
            .setDuration(500)
            .start()
    }

    override fun onInterceptBackPressed(): Boolean {
//        val currentItem = bvGuide.currentItem
//        return if (currentItem > 0) {
//            bvGuide.setCurrentItem(currentItem - 1, true)
//            //禁用返回键
//            true
//        } else {
//            !finishHelper.canFinish()
//        }
        return !finishHelper.canFinish()
    }

}
