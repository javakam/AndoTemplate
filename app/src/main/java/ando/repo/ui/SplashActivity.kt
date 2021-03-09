package ando.repo.ui

import ando.repo.config.AppRouter
import ando.repo.R
import ando.toolkit.ext.doAsyncDelay
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * # SplashActivity
 *
 * @author javakam
 * @date 2021/3/4  10:42
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!this.isTaskRoot) {
            intent?.apply {
                if (hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == action) {
                    finish()
                    return
                }
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        doAsyncDelay({
            AppRouter.toMain(this@SplashActivity)
            finish()
        }, 300)
    }

}