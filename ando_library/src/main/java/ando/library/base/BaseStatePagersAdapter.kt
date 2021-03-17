package ando.library.base

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

//https://blog.csdn.net/c6E5UlI1N/article/details/90307961
abstract class BaseStatePagersAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_SET_USER_VISIBLE_HINT) {

    protected var mTitles: List<String>? = null
    protected var mFragments: List<BaseFragment>? = null

    fun setData(fragments: List<BaseFragment>?, titles: List<String>?) {
        this.mFragments = fragments
        this.mTitles = titles
    }

    fun updateData(fragments: List<BaseFragment>?, titles: List<String>?) {
        setData(fragments, titles)
        notifyDataSetChanged()
    }

    override fun getItem(i: Int): BaseFragment = mFragments?.get(i) as BaseFragment

    override fun getPageTitle(position: Int): CharSequence? =
        mTitles?.get(position) as CharSequence

    override fun getCount(): Int = mTitles?.size ?: 0

    override fun getItemPosition(`object`: Any): Int = POSITION_NONE

}