package ando.repo.ui

import ando.repo.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * # TabLayout + ViewPager2
 *
 * @author javakam
 * @date 2021/3/17  16:48
 */
class TabLayoutActivity : AppCompatActivity() {

    private val mTabLayout: TabLayout by lazy { findViewById(R.id.tabLayout) }
    private val mViewPager: ViewPager2 by lazy { findViewById(R.id.viewpager) }
    private val mViewPagerAdapter: TabFragmentStateAdapter<TabItem> by lazy { TabFragmentStateAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_tablayout)

        //Data
        val data = mutableListOf(
            TabItem("2387", "简介"),
            TabItem("3589", "作品"),
            TabItem("4212", "推荐"),
        )

        mViewPagerAdapter.setData(data)
        mViewPager.adapter = mViewPagerAdapter
        mViewPager.offscreenPageLimit = 100
        mViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val mediator = TabLayoutMediator(
            mTabLayout, mViewPager
        ) { tab: TabLayout.Tab, pos: Int ->
            tab.text = data[pos].name
        }
        mediator.attach()
    }

    data class TabItem(var id: String, val name: String)

    inner class TabFragmentStateAdapter<T> :
        FragmentStateAdapter(supportFragmentManager, lifecycle) {
        private lateinit var mList: List<T>

        fun setData(data: List<T>) {
            this.mList = data
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = mList.size

        override fun createFragment(position: Int): Fragment {
            return DisplayFragment.newInstance(position)
        }
    }
}