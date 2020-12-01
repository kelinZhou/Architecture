package com.kelin.architecture.ui.base.flyweight.adapter

import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kelin.architecture.core.SystemError
import com.kelin.architecture.ui.base.BasicFragment
import com.kelin.architecture.ui.base.common.CommonErrorFragment
import java.util.*

/**
 * 描述 FragmentPager {ViewPager}的适配器。
 * 创建人 kelin
 * 创建时间 16/9/18  上午11:44
 * 包名 com.chengshi.iot.adapter
 */
class CommonFragmentStatePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    /**
     * 当前{ViewPager}的所有页面的[Class]对象。
     */
    private val mClsList: MutableList<Class<out BasicFragment>> = ArrayList()

    /**
     * 当前{ViewPager}的所有页面的名字。
     */
    private val mNameList: MutableList<String> = ArrayList()

    /**
     * 当前{ViewPager}的所有页面的[BasicFragment]对象。
     */
    private val mFragmentMap: SparseArray<BasicFragment>  = SparseArray()

    /**
     * 是否保存状态。
     */
    private var mSaveState = true


    override fun saveState(): Parcelable? {
        return if (mSaveState) super.saveState() else null
    }

    fun setSaveState(saveState: Boolean) {
        mSaveState = saveState
    }

    /**
     * 添加页面。
     *
     * @param name 要添加的页面的名字。这个名字将会显示在
     * {TabLayout 页签}中。
     * @param cls  要添加的页面的[Class]对象。
     */
    fun addPager(name: String, cls: Class<out BasicFragment>) {
        //防止添加重复的页面。
        if (mNameList.contains(name)) {
            throw RuntimeException("the name: “$name” is contains!")
        }
        //健壮性判断，防止添加失败。
        if (mNameList.add(name)) {
            if (!mClsList.add(cls)) {
                mNameList.remove(name)
            }
        }
    }

    /**
     * 添加页面。
     *
     * @param name     要添加的页面的名字。这个名字将会显示在
     * {TabLayout 页签}中,如果你的页面中有的话。
     * @param fragment 要添加的页面的[BasicFragment]对象。
     */
    fun addPager(name: String, fragment: BasicFragment) {
        //防止添加重复的页面。
        if (mNameList.contains(name)) {
            RuntimeException("the name: “$name” is contains!").printStackTrace()
        }
        //健壮性判断，防止添加失败。
        if (mNameList.add(name)) {
            if (!mClsList.add(fragment.javaClass)) {
                mNameList.remove(name)
            } else {
                mFragmentMap.put(mFragmentMap.size(), fragment)
            }
        }
    }

    override fun getCount(): Int {
        return mClsList.size
    }

    override fun getItem(position: Int): BasicFragment {
        // Log.d("CommonFragmentStatePagerAdapter", "getItem: " + position);
        return getInstance(position)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // Log.d("CommonFragmentStatePagerAdapter", "instantiateItem: " + position);
        val fragment = super.instantiateItem(container, position) as BasicFragment
        mFragmentMap.put(position, fragment)
        return fragment
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)

//        Fragment fragment = (Fragment) object;
//        if (fragment != null) {
        // 可能出现fragment == mCurrentPrimaryItem，从而setUserVisibleHint(true)无法执行
//            fragment.setMenuVisibility(true);
//            fragment.setUserVisibleHint(true);
//        }
        // int old = mPrimaryItem == null ? 0 : mPrimaryItem.hashCode();
        // Log.d("CommonFragmentStatePagerAdapter", "setPrimaryItem: " + position + " " + old + "<==>" + object.hashCode());
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // Log.d("CommonFragmentStatePagerAdapter", "destroyItem: " + position);
        super.destroyItem(container, position, `object`)
    }

    /**
     * 根据索引获取[BasicFragment]。
     *
     * @param position 要获取的Fragment的索引。
     * @return 返回一个 [BasicFragment]。
     */
    private fun getInstance(position: Int): BasicFragment {
        var fragment = mFragmentMap.get(position)
        if (fragment != null) {
            return fragment
        } else {
            try {
                fragment = mClsList[position].newInstance()
                mFragmentMap.put(position, fragment)
            } catch (e: Exception) {
                e.printStackTrace()
                fragment = CommonErrorFragment.createInstance(SystemError.NEW_INSTANCE_FAILED)
            }
        }
        return fragment
    }

    /**
     * 获取指定页面的标题。
     * 主要是用来给{TabLayout 页签}设置标题。
     *
     * @param position 要获取页面的position。
     * @return 返回当前页面的标题。
     */
    override fun getPageTitle(position: Int): CharSequence? {
        return mNameList[position]
    }
    /**
     * 清空页面。
     *
     * @param isRefresh 是否在清空之后刷新页面。
     */
    @JvmOverloads
    fun clear(isRefresh: Boolean = true) {
        mNameList.clear()
        mClsList.clear()
        mFragmentMap.clear()
        if (isRefresh) {
            notifyDataSetChanged()
        }
    }
}