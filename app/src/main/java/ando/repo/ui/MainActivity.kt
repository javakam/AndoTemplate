package ando.repo.ui

import ando.repo.R
import ando.repo.config.AppRouter
import ando.repo.config.click
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        siuuuuuuuuuu()
    }

    private fun siuuuuuuuuuu() {
        click(R.id.bt_widget_tab_layout) {
            AppRouter.toWidgetTabLayout(this)
        }

        //ando indicator 👉 Google TabLayout 的替代品
        click(R.id.bt_widget_indicator) {
            AppRouter.toWidgetIndicator(this)
        }

        //ando banner & ando indicator
        click(R.id.bt_widget_banner) {
            AppRouter.toWidgetBanner(this)
        }

        //ando banner guide
        click(R.id.bt_widget_banner_guide) {
            AppRouter.toWidgetBannerGuide(this)
        }

        //SupperButton
        click(R.id.bt_widget_button) {
            AppRouter.toWidgetButton(this)
        }

        //RecyclerView
        click(R.id.bt_widget_recycler) {
            AppRouter.toWidgetRecycler(this)
        }

        //SmartDragLayout
        click(R.id.bt_widget_drag) {
            AppRouter.toWidgetDrag(this)
        }

    }

}