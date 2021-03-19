package ando.repo.ui.indicator

import ando.repo.R
import ando.repo.bean.ChannelBean
import ando.repo.ui.DisplayFragment
import ando.repo.widget.indicator.MagicIndicatorHelper
import ando.toolkit.ThreadTask
import ando.toolkit.ThreadUtils
import ando.widget.indicator.FragmentContainerHelper
import ando.widget.indicator.MagicIndicator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * # 顶部导航 + Fragment + 不带ViewPager
 *
 * @author javakam
 * @date 2021/3/18  16:03
 */
class IndicatorViewPagerNoActivity : AppCompatActivity() {

    private var mCurrentFragment: Fragment? = null

    private val mIndicator: MagicIndicator by lazy { findViewById(R.id.indicator) }
    private var mIndicatorFixedAdapter: IndicatorFixedNavAdapter =
        IndicatorFixedNavAdapter()
    private var mIndicatorHelper: MagicIndicatorHelper? = null

    private val mChannels: MutableList<ChannelBean> = mutableListOf() //标题
    private val mFragments: MutableList<Fragment> = mutableListOf()
    private var mFragmentContainerHelper: FragmentContainerHelper = FragmentContainerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        remoteData {
            if (!it) {
                mIndicatorFixedAdapter.updateData(mChannels)

                switchIndicator(0)
                switchFragment(0)
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_indicator_viewpager_no)

        mIndicatorHelper = MagicIndicatorHelper(this, supportFragmentManager)
        mIndicatorHelper?.bind(mIndicator, null)

        initIndicator()
    }

    /**
     * 模拟网络数据
     */
    private fun remoteData(callBack: (isEmpty: Boolean) -> Unit = {}) {
        dataaaaaaa { cs: List<ChannelBean>? ->
            if (cs.isNullOrEmpty()) {
                //空视图处理
                callBack.invoke(true)
                return@dataaaaaaa
            }

            mChannels.clear()
            mFragments.clear()

            mChannels.addAll(cs)
            cs.forEachIndexed { i, c ->
                mFragments.add(DisplayFragment.newInstance(i, c.title))
            }
            callBack.invoke(false)
        }
    }

    private fun initIndicator() {
        //顶部导航切换
        mIndicatorFixedAdapter.setOnTabClickListener { i: Int ->
            switchIndicator(i)
            switchFragment(i)
        }
        mIndicatorHelper?.initIndicatorNoVp(this, mIndicatorFixedAdapter)
        //ViewPagerHelper.bind(magicIndicator, mViewPager)
        mFragmentContainerHelper.attachMagicIndicator(mIndicator)
    }

    private fun switchIndicator(index: Int) {
        if (mFragments.isNotEmpty() && index < mFragments.size)
            mFragmentContainerHelper.handlePageSelected(index, false)
    }

    private fun switchFragment(index: Int) {
        if (mFragments.isNotEmpty() && index < mFragments.size) switchFragment(mFragments[index])
    }

    private fun switchFragment(fragment: Fragment) {
        if (mCurrentFragment == fragment) return
        supportFragmentManager
            .beginTransaction()
            .apply {
                mCurrentFragment?.let { if (it.isAdded) hide(it) }
                if (fragment.isAdded) {
                    show(fragment)
                } else {
                    add(R.id.container, fragment)
                }
            }
            .commitAllowingStateLoss()

        mCurrentFragment = fragment
    }

    private fun dataaaaaaa(block: (List<ChannelBean>?) -> Unit) {
        ThreadUtils.executeByCpu(ThreadTask({
            val channels: List<ChannelBean> = mutableListOf(
                ChannelBean("a1", "推荐"),
                ChannelBean("a2", "热门"),
                ChannelBean("a3", "追番"),
            )
            channels
        }, {
            block.invoke(it)
        }))
    }

}