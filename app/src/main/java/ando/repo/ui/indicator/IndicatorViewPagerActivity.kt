package ando.repo.ui.indicator

import ando.repo.R
import ando.repo.bean.ChannelBean
import ando.repo.ui.DisplayFragment
import ando.repo.widget.indicator.MagicIndicatorHelper
import ando.toolkit.ThreadTask
import ando.toolkit.ThreadUtils
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
    private val mViewPager: ViewPager by lazy { findViewById(R.id.viewpager) }

    private var mIndicatorHelper: MagicIndicatorHelper? = null

    //
    private val mChannels: MutableList<ChannelBean> = mutableListOf() //标题
    private val mFragments: MutableList<Fragment> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        remoteData {
            if (it) {
                //空视图处理
            } else {
                mIndicatorHelper?.initIndicatorVp(mChannels, mFragments)
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_indicator_viewpager)

        mIndicatorHelper = MagicIndicatorHelper(this, supportFragmentManager)
        mIndicatorHelper?.bind(mIndicator, mViewPager)

        //样式二
        initIndicator2()
    }

    private fun initIndicator2() {
        //todo 2021年3月18日 16:42:38
        //mIndicator2
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
            )
            channels
        }, {
            block.invoke(it)
        }))
    }

}