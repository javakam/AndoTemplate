package ando.repo.ui.coordinator

import ando.repo.R
import ando.repo.ui.DisplayFragment
import ando.toolkit.ext.DeviceUtils.getStatusBarHeight
import ando.toolkit.StatusBarUtils
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * # TabLayout + ViewPager2
 *
 * https://juejin.cn/post/6844903518982111245
 *
 * @author javakam
 * @date 2021/3/17  16:48
 */
open class TabLayoutActivity : AppCompatActivity() {

    private val mTabLayout: TabLayout by lazy { findViewById(R.id.tabLayout) }
    private val mViewPager: ViewPager2 by lazy { findViewById(R.id.viewpager2) }
    private val mViewPagerAdapter: TabFragmentStateAdapter<TabItem> by lazy { TabFragmentStateAdapter() }

    private fun initImmersive() {
        StatusBarUtils.translucent(window)
        //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //window?.statusBarColor = Color.parseColor("#E0000000")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        initImmersive()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_tablayout)
        AndroidBug5497Workaround.assistActivity(
            findViewById(android.R.id.content)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.root_container_layout), object : OnApplyWindowInsetsListener {
                    override fun onApplyWindowInsets(
                        v: View?,
                        insets: WindowInsetsCompat
                    ): WindowInsetsCompat {
                        @Suppress("DEPRECATION")
                        return insets.consumeSystemWindowInsets()
                    }
                })
        }

        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.tool_bar)
        toolbar.title = "Title"
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setStatusBarPaddingAndHeight(toolbar)

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

    private fun setStatusBarPaddingAndHeight(toolBar: View?) {
        if (toolBar == null) return

        val statusBarHeight: Int = getStatusBarHeight()
        toolBar.setPadding(
            toolBar.paddingLeft, statusBarHeight, toolBar.paddingRight,
            toolBar.paddingBottom
        )

        val toolBarHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            45F, resources.displayMetrics
        ).toInt()
        toolBar.layoutParams.height = statusBarHeight + toolBarHeight
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