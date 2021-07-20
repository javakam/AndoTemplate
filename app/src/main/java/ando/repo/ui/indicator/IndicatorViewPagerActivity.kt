package ando.repo.ui.indicator

import ando.repo.R
import ando.repo.bean.ChannelBean
import ando.repo.ui.DisplayFragment
import ando.repo.widget.indicator.MagicIndicatorHelper
import ando.toolkit.ThreadUtils
import ando.toolkit.thread.ThreadTask
import ando.widget.indicator.MagicIndicator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

/**
 * # 顶部导航 + Fragment + ViewPager
 *
 * @author javakam
 * @date 2021/3/18  16:03
 */
class IndicatorViewPagerActivity : AppCompatActivity() {
    private val mIndicator: MagicIndicator by lazy { findViewById(R.id.indicator) }
    private val mIndicator2: MagicIndicator by lazy { findViewById(R.id.indicator2) }
    private val mIndicator3: MagicIndicator by lazy { findViewById(R.id.indicator3) }
    private val mViewPager: ViewPager by lazy { findViewById(R.id.viewpager) }

    private var mIndicatorHelper: MagicIndicatorHelper? = null
    private var mIndicatorHelper2: MagicIndicatorHelper? = null
    private var mIndicatorHelper3: MagicIndicatorHelper? = null

    //
    private val mChannels: MutableList<ChannelBean> = mutableListOf() //标题
    private val mFragments: MutableList<Fragment> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        remoteData {
            if (it) {
                //空视图处理
            } else {
                //样式一
                mIndicatorHelper = MagicIndicatorHelper(this, supportFragmentManager)
                mIndicatorHelper?.bind(mIndicator, mViewPager)
                mIndicatorHelper?.initIndicatorStyle1(mChannels, mFragments)

                //样式二
                mIndicatorHelper2 = MagicIndicatorHelper(this, supportFragmentManager)
                mIndicatorHelper2?.bind(mIndicator2, mViewPager)
                mIndicatorHelper2?.initIndicatorStyle2(mChannels, mFragments)

                //样式三
                mIndicatorHelper3 = MagicIndicatorHelper(this, supportFragmentManager)
                mIndicatorHelper3?.bind(mIndicator3, mViewPager)
                mIndicatorHelper3?.initIndicatorStyle3(mChannels, mFragments)

            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_indicator_viewpager)
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

    private fun dataaaaaaa(block: (List<ChannelBean>?) -> Unit) {
        ThreadUtils.executeByCpu(ThreadTask({
            val channels: List<ChannelBean> = mutableListOf(
                ChannelBean("a1", "推荐"),
                ChannelBean("a2", "热门"),
                ChannelBean("a3", "追番"),
                ChannelBean("a4", "直播"),
                ChannelBean("a5", "影视"),
                ChannelBean("a5", "漫画"),
            )
            channels
        }, {
            block.invoke(it)
        }))
    }

}