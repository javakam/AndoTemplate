package ando.toolkit.ext

import android.content.ClipboardManager
import android.content.Context
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/**
 * @author javakam
 * @date 2020/11/9  16:53
 */

inline val Context.vibratorManager: android.os.Vibrator
    get() = getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator

inline val Context.layoutInflater: android.view.LayoutInflater
    get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater

inline val Context.windowManager: WindowManager
    get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager

inline val Context.inputMethodManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

inline val Context.clipboardManager: ClipboardManager
    get() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

inline val Fragment.vibratorManager: android.os.Vibrator?
    get() = activity?.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator?

inline val Fragment.windowManager: WindowManager?
    get() = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager?

inline val Fragment.inputMethodManager: InputMethodManager?
    get() = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

inline val Fragment.clipboardManager: ClipboardManager?
    get() = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?