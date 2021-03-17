package ando.repo.config

import ando.toolkit.ext.noShake
import android.app.Activity
import android.view.View

inline fun Activity.click(id: Int, crossinline block: () -> Unit) {
    findViewById<View>(id)?.noShake {
        block()
    }
}