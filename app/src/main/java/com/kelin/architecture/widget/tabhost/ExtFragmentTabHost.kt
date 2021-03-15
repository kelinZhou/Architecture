package com.kelin.architecture.widget.tabhost

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.FragmentTabHost
import java.util.*

class ExtFragmentTabHost @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FragmentTabHost(context, attrs) {
    private val mTabs = ArrayList<String>()

    override fun addTab(tabSpec: TabSpec) {
        super.addTab(tabSpec)
        mTabs.add(tabSpec.tag)
    }

    override fun addTab(tabSpec: TabSpec, clss: Class<*>, args: Bundle?) {
        super.addTab(tabSpec, clss, args)
        mTabs.add(tabSpec.tag)
    }

    override fun onTabChanged(tabId: String?) {
        try {
            super.onTabChanged(tabId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAttachedToWindow() {
        try {
            super.onAttachedToWindow()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}