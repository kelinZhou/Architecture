package com.kelin.architecture.ui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.TaskStackBuilder
import androidx.fragment.app.Fragment
import com.kelin.okpermission.OkActivityResult
import com.kelin.architecture.R
import com.kelin.architecture.annotation.SoftInputModeFlags
import com.kelin.architecture.core.AndroidBug5497Workaround
import com.kelin.architecture.tools.BackHandlerHelper
import com.kelin.architecture.util.LogHelper
import com.kelin.architecture.tools.statusbar.StatusBarHelper
import com.kelin.architecture.util.dp2px
import kotlin.reflect.KClass

/**
 * **描述:** 所有Activity的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-06 09:04
 *
 * **版本:** v 1.0.0
 */
abstract class BasicActivity : AppCompatActivity() {
    /**
     * 用来记录当前页面的Toolbar控件。
     */
    var toolbar: Toolbar? = null
    /**
     * 用来记录当前页面的标题控件。
     */
    private var tvTitle: TextView? = null
    /**
     * 用来记录当前页面的子标题控件。
     */
    private var tvSubtitle: TextView? = null

    var isActivityResumed = false
        private set

    protected var isDarkMode: Boolean = false

    protected open val hasFullScreen: Boolean = true

    private var mTitleCenter = true

    private val statusBarHelper: StatusBarHelper by lazy { StatusBarHelper(this) }

    protected val toolbarPosition: IntArray
        get() {
            val location = IntArray(2)
            if (toolbar != null) {
                toolbar!!.getLocationInWindow(location)
                location[1] = location[1] + toolbar!!.height - StatusBarHelper.getStatusBarHeight(this)
            }
            return location
        }

    val isActivityDestroyed: Boolean
        get() = (isDestroyed) || isFinishing


    override fun startActivity(intent: Intent?, options: Bundle?) {
        super.startActivity(intent, options)
        overridePendingTransition(R.anim.anim_translate_x100_x0_300, R.anim.anim_translate_x0_x100minus_300)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        overridePendingTransition(R.anim.anim_translate_x100_x0_300, R.anim.anim_translate_x0_x100minus_300)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_translate_x100minus_x0_300, R.anim.anim_translate_x0_x100_300)
    }

    protected fun setNavigationIcon(@DrawableRes iconId: Int) {
        if (iconId != 0) {
            toolbar?.setNavigationIcon(iconId)
        } else {
            toolbar?.navigationIcon = null
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val fm = supportFragmentManager
        @SuppressLint("RestrictedApi") val frags = fm.fragments
        for (f in frags) {
            if (f is BasicFragment && event != null) {
                val isConsum = f.dispatchKeyEvent(event)
                return if (isConsum) isConsum else super.dispatchKeyEvent(event)
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title ?: "")
    }

    protected open fun <V : View?> getView(@IdRes viewId: Int): V? {
        return findViewById<V>(viewId)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //Android8.0如果Activity是透明的则会崩溃，但是小米手机上调用这行代码虽然会崩溃但仍然可以实现屏幕锁定。所以这里用try catch。
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } catch (ignore: Exception) {
            LogHelper.system.d("=========锁定屏幕方向失败：", this.javaClass.simpleName)
        }
    }

    final override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        if (hasFullScreen) {
            AndroidBug5497Workaround.assistActivity(this)
        }
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        if (hasFullScreen) {
            AndroidBug5497Workaround.assistActivity(this)
        }
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        if (hasFullScreen) {
            AndroidBug5497Workaround.assistActivity(this)
        }
    }

    override fun onStart() {
        super.onStart()
        if (isDarkMode) {
            StatusBarHelper.setStatusBarDarkMode(this)
        } else {
            StatusBarHelper.setStatusBarLightMode(this)
        }
    }

    protected fun disableHomeAsUp() {
        setNavigationIcon(0)
    }

    override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        if (mTitleCenter) {
            tvTitle?.text = title
//            tvTitle?.setTextColor(color)
        } else {
            toolbar?.title = title
//            toolbar?.setTitleTextColor(color)
        }
    }

    fun setTitle(title: CharSequence?, center: Boolean) {
        mTitleCenter = center
        super.setTitle(title)
    }

    fun setSubTitle(title: CharSequence, center: Boolean) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            if (center) {
                if (tvSubtitle != null) {
                    tvSubtitle!!.visibility = View.VISIBLE
                    tvSubtitle!!.text = title
                }
            } else {
                actionBar.setDisplayShowTitleEnabled(true)
                actionBar.subtitle = title
            }
            mTitleCenter = center
        }
    }

    /**
     * 覆盖软键盘模式。调用改方法前必须确保[.overrideWindowSoftInputModeEnable]返回true，否则该方法不会生效。
     *
     * @param mode 要设置的模式。
     * @see .overrideWindowSoftInputModeEnable
     */
    fun overrideWindowSoftInputMode(@SoftInputModeFlags mode: Int) {
        if (overrideWindowSoftInputModeEnable()) {
            window.setSoftInputMode(mode)
        }
    }

    /**
     * 是否可以覆盖软键盘的弹出模式。
     *
     * 如果当前的Activity中包含了[BasicFragment]实例，
     * 并且覆盖了[BasicFragment.windowSoftInputModeFlags]方法或者调用了 [ ][BasicFragment.overrideWindowSoftInputMode]方法的话会导致[BaseActivity]本身所配置的软键盘启动模式失效，
     * 无论是通过清单文件配置还是代码配置，在[BasicFragment]启动后都会失效，如果你希望在这种情况下以[BaseActivity]为准，
     * 则需要覆盖该方法并返回false。如果返回了false，则[.overrideWindowSoftInputMode]方法就会失效。
     *
     * @return 如果允许覆盖则返回true，否则返回false。默认实现为可以覆盖。
     * @see .overrideWindowSoftInputMode
     * @see BasicFragment.overrideWindowSoftInputMode
     * @see BasicFragment.windowSoftInputModeFlags
     */
    open fun overrideWindowSoftInputModeEnable(): Boolean {
        return true
    }


    fun initTitleBar(
        toolbar: Toolbar?,
        titleView: TextView?,
        subtitleView: TextView?,
        needNavigationIcon: Boolean = true
    ) {
        if (toolbar != null) {
            this.toolbar = toolbar
            this.tvTitle = titleView?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    transitionName = getString(R.string.transition_name_common_activity_title)
                }
            }
            this.tvSubtitle = subtitleView
            setSupportActionBar(toolbar)

            if (titleView != null) {
                titleView.compoundDrawablePadding = 5f.dp2px
            }
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false)
                actionBar.setDisplayHomeAsUpEnabled(false)
            }
            if (needNavigationIcon) {
                setNavigationIcon(if (isDarkMode) R.drawable.ic_navigation_whit else R.drawable.ic_navigation_black)
            }
            val parent = toolbar.parent as? View
            if (parent != null) {
                statusBarHelper.setActionBarView(parent)
            }
//            toolbar.setTitleTextAppearance(this, R.style.common_title_text_style)
        }
    }

    protected fun onToolBarLeftClick() {
        finish()
    }

    fun processStatusBar(@ColorInt color: Int) {
        statusBarHelper.process(color)
        if (isDarkMode) {  //这里加这语句是因为如果onStart中的代码执行之后又执行了这里会导致设置失效。
            StatusBarHelper.setStatusBarDarkMode(this)
        } else {
            StatusBarHelper.setStatusBarLightMode(this)
        }
    }


    @JvmOverloads
    protected fun addFragment(containerViewId: Int, fragment: Fragment, tag: String = fragment.javaClass.simpleName) {
        this.supportFragmentManager
            .beginTransaction()
            .add(containerViewId, fragment, tag)
            .commit()
    }


    protected fun replaceFragmentV4(containerViewId: Int, fragment: Fragment) {
        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(containerViewId, fragment, fragment.javaClass.simpleName)
        fragmentTransaction.setCustomAnimations(
            R.anim.anim_translate_x100_x0_300,
            R.anim.anim_translate_x0_x100minus_300,
            R.anim.anim_translate_x100minus_x0_300,
            R.anim.anim_translate_x0_x100_300
        )
        fragmentTransaction.commitAllowingStateLoss()
    }


    @JvmOverloads
    protected fun pushFragmentV4(
        containerViewId: Int,
        fragment: Fragment,
        tag: String = fragment.javaClass.simpleName
    ) {
        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(containerViewId, fragment, tag).addToBackStack(null)
        fragmentTransaction.commitAllowingStateLoss()
    }

    public override fun onResume() {
        super.onResume()
        isActivityResumed = true
    }

    public override fun onPause() {
        isActivityResumed = false
        super.onPause()
    }

    override fun onSupportNavigateUp(): Boolean {
        LogHelper.system.d(javaClass.simpleName, "onSupportNavigateUp")
        return super.onSupportNavigateUp()
    }

    override fun onCreateSupportNavigateUpTaskStack(builder: TaskStackBuilder) {
        super.onCreateSupportNavigateUpTaskStack(builder)

        LogHelper.system.d(javaClass.simpleName, "onCreateSupportNavigateUpTaskStack")
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        LogHelper.system.d(javaClass.simpleName, "onPrepareOptionsMenu")
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onOptionsMenuClosed(menu: Menu) {
        LogHelper.system.d(javaClass.simpleName, "onOptionsMenuClosed")
        super.onOptionsMenuClosed(menu)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        LogHelper.system.d(javaClass.simpleName, "onMenuOpened")
        return super.onMenuOpened(featureId, menu)
    }

    override fun onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed()
        }
    }


    protected fun createFragmentByClass(clazz: Class<out Fragment>, intent: Intent) =
        BasicFragment.newInstance(clazz, intent.extras)

    companion object {
        fun generateJumpIntent(context: Context, activityClass: Class<out BasicActivity>): Intent {
            val intent = Intent(context, activityClass)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            return intent
        }
    }
}

fun <D> Intent.startForResult(context: Activity, onResult: (resultCode: Int, data: D?) -> Unit) {
    OkActivityResult.startActivity(context, this, onResult =  onResult)
}

fun Intent.startForResult(context: Activity, onResult: (resultCode: Int) -> Unit) {
    OkActivityResult.startActivity(context, this, onResult =  onResult)
}


fun KClass<out Fragment>.create(intent: Intent): Fragment {
    return BasicFragment.newInstance(java, intent.extras)
}