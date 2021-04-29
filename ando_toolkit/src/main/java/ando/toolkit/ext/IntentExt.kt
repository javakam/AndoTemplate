package ando.toolkit.ext

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment

/**
 * # Intent
 *
 * @author javakam
 * @date 2020/9/29  15:00
 */

/* ---------- From Anko ---------- */

/**
 *  send sms
 */
fun Fragment.sendSMS(number: String, text: String = ""): Boolean =
    activity?.sendSMS(number, text) ?: false

/**
 * @param number
 * @param text sms body
 */
fun Context.sendSMS(number: String, text: String = ""): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$number"))
        intent.putExtra("sms_body", text)
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * call phone
 */
@RequiresPermission(value = "android.permission.CALL_PHONE")
fun Fragment.makeCall(number: String): Boolean = activity?.makeCall(number) ?: false

/**
 * @param number phone number
 */
@RequiresPermission(value = "android.permission.CALL_PHONE")
fun Context.makeCall(number: String): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * send email
 */

fun Fragment.email(email: String, subject: String = "", text: String = "") =
    activity?.email(email, subject, text)

/**
 * @param email
 * @param subject
 * @param text
 */
@SuppressLint("QueryPermissionsNeeded")
fun Context.email(email: String, subject: String = "", text: String = ""): Boolean {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    if (subject.isNotEmpty())
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    if (text.isNotEmpty())
        intent.putExtra(Intent.EXTRA_TEXT, text)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
        return true
    }
    return false
}

/**
 * shared
 */
fun Fragment.share(text: String, subject: String = "") = activity?.share(text, subject)

/**
 * @param text
 * @param subject
 */
fun Context.share(text: String, subject: String = "", title: String? = null): Boolean {
    return try {
        val intent = Intent(android.content.Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(android.content.Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, title))
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}

/**
 * browse web
 */
fun Fragment.browser(url: String, newTask: Boolean = false) = activity?.browser(url, newTask)

/**
 * @param url
 * @param newTask
 */
fun Context.browser(url: String, newTask: Boolean = false): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        //startActivity(intent)
        //https://developer.android.com/about/versions/11/privacy/package-visibility
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, "请选择浏览器"))
        } else {
            toastShort("没有可用浏览器")
        }
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}