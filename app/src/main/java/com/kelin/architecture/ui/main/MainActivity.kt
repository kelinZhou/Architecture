package com.kelin.architecture.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kelin.architecture.R
import com.kelin.architecture.core.AppModule
import com.kelin.architecture.tools.FinishHelper
import com.kelin.architecture.ui.base.BasicActivity
import com.kelin.architecture.ui.base.start
import com.kelin.architecture.widget.tabhost.TabsCreator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BasicActivity() {

    companion object{

        private const val KEY_MAIN_PAGE_DEF_INDEX = "key_main_page_def_index"

        fun start(context: Context, defIndex: Int) {
            Intent(context, MainActivity::class.java).apply {
                putExtra(KEY_MAIN_PAGE_DEF_INDEX, defIndex)
            }.start(context)
        }
    }


    private val defIndex: Int by lazy { intent.getIntExtra(KEY_MAIN_PAGE_DEF_INDEX, 0) }

    private val exitHelper = FinishHelper.createInstance()

    private val tabsCreator by lazy { TabsCreator() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tabsCreator.setupView(
            this,
            tabHost,
            TabsManager.values(),
            defIndex,
            R.id.real_content,
            supportFragmentManager
        )
    }

    override fun onBackPressed() {
        if (exitHelper.canFinish()) {
            AppModule.finishAllActivities()
        }
    }
}