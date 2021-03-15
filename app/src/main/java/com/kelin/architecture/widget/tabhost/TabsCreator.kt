package com.kelin.architecture.widget.tabhost

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TabHost
import android.widget.TabHost.*
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTabHost
import com.kelin.architecture.R
import com.kelin.architecture.util.visibleOrGone

class TabsCreator {
    private var mMsgCount: TextView? = null
    @JvmOverloads
    fun setupView(context: Context?, tabHost: FragmentTabHost, tabs:Array<out HostTab>, initialIndex: Int, containerId: Int, fragmentManager: FragmentManager?, onTabChangeListener: ((tabId:String) -> Unit)? = null) {
        tabHost.setup(context!!, fragmentManager!!, containerId)
        tabHost.tabWidget.dividerDrawable = null
        tabHost.setOnTabChangedListener(onTabChangeListener)
        for (tab in tabs) {
            tabHost.addTab(addNewTab(tabHost, tab.tag, tab.tabName, tab.icon, tab.icon), tab.cls, null)
        }
        if (initialIndex < 0) {
            tabHost.currentTab = 0
        } else {
            tabHost.currentTab = initialIndex
        }
    }

    private fun mapTabPositionToId(position: Int): Int {
        return position
    }

    fun showMsgCountHint(num: Int) {
        mMsgCount?.apply {
            text = num.toUnreadCount()
            visibleOrGone = num > 0
        }
    }

    internal class DummyTabFactory(private val mContext: Context) : TabContentFactory {
        override fun createTabContent(tag: String): View {
            val v = View(mContext)
            v.minimumWidth = 0
            v.minimumHeight = 0
            return v
        }

    }

    private fun addDummyTab(context: Context, tabHost: TabHost, tag: String, position: Int, layout: Int): TabSpec {
        val tabSpec = tabHost.newTabSpec(tag)
        val view = LayoutInflater.from(context).inflate(layout, null)
        view.id = mapTabPositionToId(position)
        tabSpec.setIndicator(view) // new function to inject our own tab toast_system_toast_layout
        //tabSpec.setContent(contentID);
//tabHost.addTab(tabSpec);
        tabSpec.setContent(DummyTabFactory(context))
        return tabSpec
    }

    private fun addNewTab(tabHost: TabHost, tag: String, title: String, icon: Int, position: Int): TabSpec {
        val tabSpec = tabHost.newTabSpec(tag)
        val v = getTabIndicator(tabHost, title, icon)
        v.id = mapTabPositionToId(position)
        tabSpec.setIndicator(v) // new function to inject our own tab toast_system_toast_layout
        //tabSpec.setContent(contentID);
//tabHost.addTab(tabSpec);
        return tabSpec
    }

    @SuppressLint("InflateParams")
    private fun getTabIndicator(tabHost: TabHost, title: String, icon: Int): View {
        val view = LayoutInflater.from(tabHost.context).inflate(R.layout.common_tab_indicator_holo, null)
        val iv = view.findViewById<ImageView>(R.id.icon)
        if (TextUtils.equals("消息", title)) {
            mMsgCount = view.findViewById(R.id.tv_msg_count)
        }
        iv.setImageResource(icon)
        val tv = view.findViewById<TextView>(R.id.title)
        tv.text = title
        return view
    }
}



fun Int.toUnreadCount(): String {
    return if (this == 0) "" else if (this > 99) "99" else toString()
}