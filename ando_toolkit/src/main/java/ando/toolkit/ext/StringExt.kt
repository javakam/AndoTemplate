package ando.toolkit.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import ando.toolkit.AppUtils
import java.util.*

/**
 * # String Extension
 *
 * @author javakam
 * @date 2020/10/29  10:07
 */

fun String?.noNull(default: String? = ""): String {
    return if (isNullOrBlank()) default ?: "" else this
}

fun CharSequence?.noNullZero(): CharSequence {
    return if (this.isNullOrBlank()) "0" else this
}

fun CharSequence?.noNullZeroInt(): Int {
    return if (this.isNullOrBlank()) 0 else this.toString().toInt()
}

/**
 * 可以在应用内播放的地址
 */
fun String?.isVideoUrl(): Boolean {
    return (this.noNull()).toLowerCase(Locale.getDefault()).run {
        endsWith(".m3u8")
                || endsWith(".mp4")
                || endsWith(".mkv")
                || endsWith(".flv")
                || endsWith(".avi")
                || endsWith(".rm")
                || endsWith(".rmvb")
                || endsWith(".wmv")
    }
}

fun String.copyToClipBoard() {
    val cm: ClipboardManager? =
        AppUtils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager?
    if (cm != null) {
        //参数一：标签，可为空，参数二：要复制到剪贴板的文本
        cm.primaryClip = ClipData.newPlainText(null, this)
        if (cm.hasPrimaryClip()) {
            cm.primaryClip?.getItemAt(0)?.text
        }
    }
}
