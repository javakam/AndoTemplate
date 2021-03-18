package ando.repo.config

import ando.repo.R
import ando.toolkit.ResUtils
import ando.toolkit.ext.noShake
import android.app.Activity
import android.view.View

fun indicatorHeight(): Int {
    return ResUtils.getDimensionPixelOffset(R.dimen.dp_45)
}

inline fun Activity.click(id: Int, crossinline block: () -> Unit) {
    findViewById<View>(id)?.noShake {
        block()
    }
}