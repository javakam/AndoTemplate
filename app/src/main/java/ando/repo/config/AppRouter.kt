package ando.repo.config

import ando.repo.ui.MainActivity
import android.app.Activity
import android.content.Intent

/**
 * # AppRouter
 *
 * @author javakam
 * @date 2021/3/4  13:39
 */
object AppRouter {

    fun toMain(activity: Activity) {
        activity.startActivity(Intent(activity, MainActivity::class.java))
    }

}