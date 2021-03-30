package ando.toolkit

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

/**
 * # 截屏工具类
 *
 * @author javakam
 * @date 2020/1/3 10:58
 */
object ScreenShotUtils {

    fun showDialog(context: Context, bitmap: Bitmap) {
        val builder = AlertDialog.Builder(context)
        val scrollView = NestedScrollView(context)
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        val imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.setImageBitmap(bitmap)
        linearLayout.addView(imageView)
        linearLayout.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        scrollView.addView(linearLayout)
        builder.setView(scrollView)
        builder.setCancelable(true)
        builder.setNegativeButton("关闭") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    fun showActivityShotDialog(activity: Activity) {
        showDialog(activity, shotActivity(activity))
    }

    fun showActivityWithoutStatusWithoutNavigationBarShotDialog(activity: Activity) {
        showDialog(activity, shotActivityWithoutStatusWithoutNavigationBar(activity))
    }

    fun showRecyclerViewShotDialog(view: RecyclerView) {
        shotRecyclerView(view)?.apply { showDialog(view.context, this) }
    }

    /**
     * https://gist.github.com/PrashamTrivedi
     */
    fun shotRecyclerView(view: RecyclerView): Bitmap? {
        val adapter = view.adapter
        var bigBitmap: Bitmap? = null
        if (adapter != null) {
            val size = adapter.itemCount
            var height = 0
            val paint = Paint()
            var iHeight = 0
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
            // Use 1/8th of the available memory for this memory cache.
            val cacheSize = maxMemory / 8
            val bitmapCache = LruCache<String, Bitmap>(cacheSize)
            var holder: RecyclerView.ViewHolder?
            for (i in 0 until size) {
                holder = adapter.createViewHolder(view, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)
                holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                holder.itemView.layout(
                    0,
                    0,
                    holder.itemView.measuredWidth,
                    holder.itemView.measuredHeight
                )
                @Suppress("DEPRECATION")
                holder.itemView.isDrawingCacheEnabled = true
                @Suppress("DEPRECATION")
                holder.itemView.buildDrawingCache()
                @Suppress("DEPRECATION")
                val drawingCache = holder.itemView.drawingCache
                if (drawingCache != null) {
                    bitmapCache.put(i.toString(), drawingCache)
                }
                height += holder.itemView.measuredHeight
            }
            bigBitmap = Bitmap.createBitmap(view.measuredWidth, height, Bitmap.Config.ARGB_8888)
            val bigCanvas = Canvas(bigBitmap)
            val lBackground = view.background
            if (lBackground is ColorDrawable) {
                val lColor = lBackground.color
                bigCanvas.drawColor(lColor)
            }
            var bitmap: Bitmap?
            for (i in 0 until size) {
                bitmap = bitmapCache[i.toString()]
                bigCanvas.drawBitmap(bitmap, 0f, iHeight.toFloat(), paint)
                iHeight += bitmap.height
                bitmap.recycle()
            }
        }
        return bigBitmap
    }

    /**
     * shot the current screen ,with the status but the status is trans *
     *
     * @param ctx current activity
     */
    @Suppress("DEPRECATION")
    fun shotActivity(ctx: Activity): Bitmap {
        val view = ctx.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bp =
            Bitmap.createBitmap(view.drawingCache, 0, 0, view.measuredWidth, view.measuredHeight)
        view.isDrawingCacheEnabled = false
        view.destroyDrawingCache()
        return bp
    }

    /**
     * shot the current screen ,with the status and navigationbar*
     */
    @Suppress("DEPRECATION")
    fun shotActivityWithoutStatusWithoutNavigationBar(ctx: Activity): Bitmap {
        val statusH = getStatusH(ctx)
        val navigationBarH = getNavigationBarHeight(ctx)
        val view = ctx.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bp = Bitmap.createBitmap(
            view.drawingCache,
            0,
            statusH,
            view.measuredWidth,
            view.measuredHeight - statusH - navigationBarH
        )
        view.isDrawingCacheEnabled = false
        view.destroyDrawingCache()
        return bp
    }

    /**
     * get the height of screen *
     */
    @Suppress("DEPRECATION")
    @SuppressLint("ObsoleteSdkInt")
    fun getScreenH(ctx: Context): Int =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2) {
            val p = Point()
            (ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.getSize(p)
            p.y
        } else {
            (ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.height
        }

    /**
     * get the width of screen **
     */
    @Suppress("DEPRECATION")
    @SuppressLint("ObsoleteSdkInt")
    fun getScreenW(ctx: Context): Int =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2) {
            val p = Point()
            (ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(p)
            p.x
        } else {
            (ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width
        }

    /**
     * get the height of status *
     */
    fun getStatusH(ctx: Activity): Int {
        val s = Rect()
        ctx.window.decorView.getWindowVisibleDisplayFrame(s)
        return s.top
    }

    /**
     * get the height of title *
     */
    fun getTitleH(ctx: Activity): Int {
        val contentTop = ctx.window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
        return contentTop - getStatusH(ctx)
    }

    /**
     * get the height of NavigationBar
     */
    fun getNavigationBarHeight(mActivity: Activity): Int {
        val resources = mActivity.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * http://blog.csdn.net/lyy1104/article/details/40048329
     */
    fun shotScrollView(scrollView: ScrollView): Bitmap? {
        var h = 0
        var bitmap: Bitmap? = null
        for (i in 0 until scrollView.childCount) {
            h += scrollView.getChildAt(i).height
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"))
        }
        bitmap = Bitmap.createBitmap(scrollView.width, h, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        scrollView.draw(canvas)
        return bitmap
    }

    /**
     * http://stackoverflow.com/questions/9791714/take-a-screenshot-of-a-whole-view
     */
    fun shotView(v: View, width: Int, height: Int): Bitmap {
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(0, 0, v.layoutParams.width, v.layoutParams.height)
        v.draw(c)
        return b
    }
}