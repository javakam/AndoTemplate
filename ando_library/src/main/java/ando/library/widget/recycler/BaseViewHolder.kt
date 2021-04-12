package ando.library.widget.recycler

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.util.Linkify
import android.util.SparseArray
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * # BaseViewHolder
 *
 * github Joan Zapata & https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 *
 * eg:
 *  ```kotlin
 *  class CustomHolder(v: View) : BaseViewHolder(v) {
 *      //...
 *  }
 *  ```
 *
 * @author javakam
 * 2021-03-11 15:38:38
 */
@Keep
open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val views: SparseArray<View> = SparseArray()

    open fun <T : View> getView(@IdRes viewId: Int): T {
        val view = getViewOrNull<T>(viewId)
        checkNotNull(view) { "No view found with id $viewId" }
        return view
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T : View> getViewOrNull(@IdRes viewId: Int): T? {
        val view = views.get(viewId)
        if (view == null) {
            itemView.findViewById<T>(viewId)?.let {
                views.put(viewId, it)
                return it
            }
        }
        return view as? T
    }

    open fun <T : View> Int.findView(): T? {
        return itemView.findViewById(this)
    }

    open fun setText(viewId: Int, value: CharSequence?): BaseViewHolder {
        getView<TextView>(viewId).text = value
        return this
    }

    open fun setText(viewId: Int, @StringRes strId: Int): BaseViewHolder {
        getView<TextView>(viewId).setText(strId)
        return this
    }

    open fun setTextColor(@IdRes viewId: Int, @ColorInt color: Int): BaseViewHolder {
        getView<TextView>(viewId).setTextColor(color)
        return this
    }

    open fun setTextColorRes(@IdRes viewId: Int, @ColorRes colorRes: Int): BaseViewHolder {
        getView<TextView>(viewId).setTextColor(ContextCompat.getColor(itemView.context, colorRes))
        return this
    }

    open fun setImageResource(@IdRes viewId: Int, @DrawableRes imageResId: Int): BaseViewHolder {
        getView<ImageView>(viewId).setImageResource(imageResId)
        return this
    }

    open fun setImageDrawable(@IdRes viewId: Int, drawable: Drawable?): BaseViewHolder {
        getView<ImageView>(viewId).setImageDrawable(drawable)
        return this
    }

    open fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap?): BaseViewHolder {
        getView<ImageView>(viewId).setImageBitmap(bitmap)
        return this
    }

    open fun setBackgroundColor(@IdRes viewId: Int, @ColorInt color: Int): BaseViewHolder {
        getView<View>(viewId).setBackgroundColor(color)
        return this
    }

    open fun setBackgroundResource(
        @IdRes viewId: Int,
        @DrawableRes backgroundRes: Int
    ): BaseViewHolder {
        getView<View>(viewId).setBackgroundResource(backgroundRes)
        return this
    }

    @SuppressLint("ObsoleteSdkInt")
    open fun setAlpha(viewId: Int, value: Float): BaseViewHolder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView<View>(viewId).alpha = value
        } else {
            // Pre-honeycomb hack to set Alpha value
            val alpha = AlphaAnimation(value, value)
            alpha.duration = 0
            alpha.fillAfter = true
            getView<View>(viewId).startAnimation(alpha)
        }
        return this
    }

    open fun setVisible(@IdRes viewId: Int, isGone: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        view.visibility = if (isGone) View.GONE else View.VISIBLE
        return this
    }

    open fun setInVisible(@IdRes viewId: Int, isVisible: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        return this
    }

    open fun setEnabled(@IdRes viewId: Int, isEnabled: Boolean): BaseViewHolder {
        getView<View>(viewId).isEnabled = isEnabled
        return this
    }

    open fun linkify(viewId: Int): BaseViewHolder {
        val view = getView<TextView>(viewId)
        Linkify.addLinks(view, Linkify.ALL)
        return this
    }

    open fun setTypeface(viewId: Int, typeface: Typeface?): BaseViewHolder {
        val view = getView<TextView>(viewId)
        view.typeface = typeface
        view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        return this
    }

    open fun setTypeface(typeface: Typeface?, vararg viewIds: Int): BaseViewHolder {
        for (viewId in viewIds) {
            val view = getView<TextView>(viewId)
            view.typeface = typeface
            view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        }
        return this
    }

    open fun setProgress(viewId: Int, progress: Int): BaseViewHolder {
        val view = getView<ProgressBar>(viewId)
        view.progress = progress
        return this
    }

    open fun setProgress(viewId: Int, progress: Int, max: Int): BaseViewHolder {
        val view = getView<ProgressBar>(viewId)
        view.max = max
        view.progress = progress
        return this
    }

    open fun setMax(viewId: Int, max: Int): BaseViewHolder {
        val view = getView<ProgressBar>(viewId)
        view.max = max
        return this
    }

    open fun setRating(viewId: Int, rating: Float): BaseViewHolder {
        val view = getView<RatingBar>(viewId)
        view.rating = rating
        return this
    }

    open fun setRating(viewId: Int, rating: Float, max: Int): BaseViewHolder {
        val view = getView<RatingBar>(viewId)
        view.max = max
        view.rating = rating
        return this
    }

    open fun setOnCheckedChangeListener(
        viewId: Int,
        listener: CompoundButton.OnCheckedChangeListener?
    ): BaseViewHolder {
        val view = getView<CompoundButton>(viewId)
        view.setOnCheckedChangeListener(listener)
        return this
    }

    open fun setTag(viewId: Int, tag: Any?): BaseViewHolder {
        val view = getView<View>(viewId)
        view.tag = tag
        return this
    }

    open fun setTag(viewId: Int, key: Int, tag: Any?): BaseViewHolder {
        val view = getView<View>(viewId)
        view.setTag(key, tag)
        return this
    }

    open fun setChecked(viewId: Int, checked: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        // View unable cast to Checkable
        if (view is CompoundButton) {
            view.isChecked = checked
        } else if (view is CheckedTextView) {
            view.isChecked = checked
        }
        return this
    }

}