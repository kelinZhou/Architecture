package com.kelin.architecture.ui.base.presenter


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.kelin.architecture.util.LogHelper
import com.kelin.architecture.ui.base.BasicActivity
import com.kelin.architecture.ui.base.BasicFragment
import com.kelin.architecture.ui.base.delegate.BaseViewDelegate
import com.kelin.architecture.ui.base.delegate.ViewDelegate
import java.lang.reflect.ParameterizedType


abstract class BaseFragmentPresenter<V : BaseViewDelegate<VC>, VC : BaseViewDelegate.BaseViewDelegateCallback> : BasicFragment(),
    ViewPresenter<VC> {

    protected var viewDelegate: V? = null
        private set

    private var mViewDelegateState: Bundle? = null

    protected open val canSwipe: Boolean
        get() = false

    protected var rootView: View? = null
        private set

    protected open val saveDelegate: Boolean
        get() = false

    protected open val hasViewDelegate: Boolean
        get() = true

    @Suppress("UNCHECKED_CAST")
    private val viewDelegateClass: Class<out V>
        get() {
            val genericSuperclass = javaClass.genericSuperclass as? ParameterizedType
            if (genericSuperclass == null) {
                throw NullPointerException("getViewDelegateClass method can't return null!")
            } else {
                val type = genericSuperclass.actualTypeArguments[0]
                if (type is Class<*> && ViewDelegate::class.java.isAssignableFrom(type)) {
                    return type as Class<out V>
                } else {
                    throw RuntimeException("the type:$type not instanceOf class!")
                }
            }
        }

    protected open fun onCreateViewSelf(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        throw RuntimeException("${javaClass.simpleName}:must implement the Method in your derived class, because hasViewDelegate field of the derived class is false.")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if (!this.hasViewDelegate) {
            this.rootView = this.onCreateViewSelf(inflater, container, savedInstanceState)
            return rootView
        } else if (this.rootView != null && this.saveDelegate) {
            val parent = this.rootView!!.parent as? ViewGroup
            parent?.removeView(this.rootView)
            return this.rootView
        } else {
            try {
                val vd = try {
                    this.viewDelegateClass.newInstance()
                } catch (e: Exception) {
                    val constructor = this.viewDelegateClass.getDeclaredConstructor()
                    constructor.isAccessible = true
                    constructor.newInstance()
                }
                this.viewDelegate = vd
                vd.onCreate(this)
                val view = vd.onCreateView(inflater, container, this.getSavedInstanceState(savedInstanceState))
                vd.bindView(this)
                this.onViewDelegateBound(vd)
                return view
            } catch (e: Exception) {
                e.printStackTrace()
                LogHelper.system.e("Create delegate error" + e.localizedMessage)
                throw RuntimeException("无法实例化: " + this.viewDelegateClass, e)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (this.hasViewDelegate) {
            this.viewDelegate!!.presentView(this, savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this.viewDelegate != null) {
            this.viewDelegate!!.saveInstanceState(outState)
        }
        if (this.mViewDelegateState != null) {
            outState.putAll(this.mViewDelegateState)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (this.hasViewDelegate) {
            this.viewDelegate!!.restoreInstanceState(savedInstanceState)
        }
    }

    override fun onDestroyView() {
        viewDelegate?.onPresenterDestroy()
        if (this.hasViewDelegate && viewDelegate != null) {
            if (!this.saveDelegate) {
                this.mViewDelegateState = Bundle()
                viewDelegate!!.destroyView(this.mViewDelegateState)
                viewDelegate!!.unbindView()
                this.viewDelegate = null
            }
        }
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (!this.saveDelegate) {
            this.mViewDelegateState = null
        }
        super.onDestroy()
    }

    private fun getSavedInstanceState(savedInstanceState: Bundle?): Bundle? {
        return if (this.mViewDelegateState == null) {
            null
        } else {
            val bundle = Bundle()
            if (this.mViewDelegateState != null) {
                bundle.putAll(this.mViewDelegateState)
            }

            if (savedInstanceState != null) {
                bundle.putAll(savedInstanceState)
            }
            bundle
        }
    }

    protected val toolbar: Toolbar?
        get() {
            val activity = activity
            return if (activity is BasicActivity) {
                activity.toolbar
            } else {
                null
            }
        }

    protected open fun onViewDelegateBound(vd: V) {}
}