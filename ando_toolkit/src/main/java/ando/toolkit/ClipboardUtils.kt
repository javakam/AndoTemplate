package ando.toolkit

import ando.toolkit.ext.clipboardManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri

object ClipboardUtils {

    private val cm: ClipboardManager by lazy { AppUtils.getContext().clipboardManager }

    /**
     * 复制文本到剪贴板
     *
     * @param text 文本
     */
    fun copyText(text: CharSequence?) {
        cm.primaryClip = ClipData.newPlainText("text", text)
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    fun getText(): CharSequence? {
        val clip: ClipData? = cm.primaryClip
        return if (clip != null && clip.itemCount > 0)
            clip.getItemAt(0).coerceToText(AppUtils.getContext())
        else null
    }

    /**
     * 复制uri到剪贴板
     *
     * @param uri uri
     */
    fun copyUri(uri: Uri?) {
        cm.primaryClip = ClipData.newUri(
            AppUtils.getContext().contentResolver, "uri", uri
        )
    }

    /**
     * 获取剪贴板的uri
     *
     * @return 剪贴板的uri
     */
    fun getUri(): Uri? {
        val clip: ClipData? = cm.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).uri
        } else null
    }

    /**
     * 复制意图到剪贴板
     *
     * @param intent 意图
     */
    fun copyIntent(intent: Intent?) {
        cm.primaryClip = ClipData.newIntent("intent", intent)
    }

    /**
     * 获取剪贴板的意图
     *
     * @return 剪贴板的意图
     */
    fun getIntent(): Intent? {
        val clip: ClipData? = cm.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).intent
        } else null
    }

}