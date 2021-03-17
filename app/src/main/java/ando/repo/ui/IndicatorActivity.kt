package ando.repo.ui

import ando.library.base.BaseMvcActivity
import ando.repo.R
import ando.repo.bean.ChannelBean
import ando.repo.widget.indicator.MagicIndicatorHelper
import ando.widget.indicator.FragmentContainerHelper
import ando.widget.indicator.MagicIndicator
import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * #
 *
 * @author javakam
 * @date 2021/3/17  16:24
 */
class IndicatorActivity :BaseMvcActivity(){
    private val mIndicator: MagicIndicator by lazy { findViewById(R.id.indicator) }
    private var mIndicatorAdapter: IndicatorNavAdapter? = null
    private var mIndicatorHelper: MagicIndicatorHelper? = null

    private val mChannels : List<ChannelBean>? = null//一级标题
    private val mFragments: List<Fragment>? = null
    private var mFragmentContainerHelper: FragmentContainerHelper? = null

    override fun initView(savedInstanceState: Bundle?) {
        mFragmentContainerHelper = FragmentContainerHelper()

        mIndicatorHelper = MagicIndicatorHelper(this, supportFragmentManager)
        mIndicatorHelper?.bind(mIndicator, null)

        mFragmentContainerHelper?.handlePageSelected(0, false)
    }

    private fun initTopIndicator() {
        mIndicatorAdapter = IndicatorNavAdapter(false)
        mIndicatorAdapter?.setData(mChannels)

        //顶部导航切换
        mIndicatorAdapter?.setOnTabClickListener {i:Int->
            mFragmentContainerHelper?.handlePageSelected(i, false)
            switchPages(i)
        }
        mIndicatorHelper?.initIndicatorHome(this, mIndicatorAdapter)
        //ViewPagerHelper.bind(magicIndicator, mViewPager)
        mFragmentContainerHelper?.attachMagicIndicator(mIndicator)
    }

    private fun switchPages(i: Int) {

    }

    private fun dataaaaaaa(){

    }


}