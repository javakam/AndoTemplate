package ando.library.widget.loader

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import ando.library.R
import android.util.Log

/**
 * # Loader
 *
 * 网络请求加载页,在请求完毕后通过[.setLoadState]设置请求结果
 *
 * @author javakam
 * @date 2019/11/15 15:03
 */
abstract class Loader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) :
    FrameLayout(context, attrs, defStyle, defStyleRes) {
    private var loadState: LoadState? = null
    private var loadingView: View? = null
    private var errorView: View? = null
    private var emptyView: View? = null
    private var mSucceedView: View? = null

    private var mRListener: OnReloadListener? = null
    private var mStateListener: OnStateListener? = null
    private var mMaxCount = 5

    private var mState = 0 // 默认的状态

    interface OnReloadListener {
        /**
         * 重新加载网络数据
         */
        fun onReload()
    }

    interface OnStateListener {
        /**
         * 状态切换，当View为空时不会切换到该状态
         *
         * @param loadState
         */
        fun onState(loadState: LoadState?)
    }

    private fun initState(attrs: AttributeSet?, defStyle: Int) {
        val theme = context.theme
        val array = theme.obtainStyledAttributes(attrs, R.styleable.Loader, defStyle, 0)
        mState = array.getInt(R.styleable.Loader_state, LoadState.UNLOADED.stateValue())
        for (loadState in LoadState.values()) {
            if (mState == loadState.stateValue()) {
                this.loadState = loadState
                break
            }
        }
    }

    private fun initView() {
        mSucceedView = createSuccessView()
        if (null != mSucceedView) {
            if (childCount == 0) {
                this.addView(
                    mSucceedView,
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                )
            }
        }
        loadingView = createLoadingView()
        if (null != loadingView) {
            this.addView(
                loadingView,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            )
        } else {
            mMaxCount--
        }
        errorView = createErrorView()
        if (null != errorView) {
            this.addView(
                errorView,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            )
        } else {
            mMaxCount--
        }
        emptyView = createEmptyView()
        if (null != emptyView) {
            this.addView(
                emptyView,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            )
        } else {
            mMaxCount--
        }
        val observer = viewTreeObserver
        observer.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                showSafePagerView()
            }
        })
    }

    /**
     * 主线程中显示界面
     */
    private fun showSafePagerView() {
        if (Thread.currentThread() === Looper.getMainLooper().thread) {
            showPagerView()
        } else {
            val handler: Handler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    if (msg.what == 0) {
                        showPagerView()
                    }
                }
            }
            handler.sendEmptyMessage(300)
        }
    }

    private fun showPagerView() {
        if (null != loadingView) {
            loadingView?.visibility =
                if (mState == LoadState.UNLOADED.stateValue() || mState == LoadState.LOADING.stateValue())
                    VISIBLE else GONE
        } else {
            Log.i(TAG, "LoadingView is null!")
        }
        if (null != errorView) {
            errorView?.visibility = if (mState == LoadState.ERROR.stateValue()) VISIBLE else GONE
            if (mStateListener != null) {
                mStateListener?.onState(LoadState.ERROR)
            }
        } else {
            Log.i(TAG, "ErrorView is null!")
        }
        if (null != emptyView) {
            emptyView?.visibility = if (mState == LoadState.EMPTY.stateValue()) VISIBLE else GONE
            if (mStateListener != null) {
                mStateListener?.onState(LoadState.EMPTY)
            }
        } else {
            Log.i(TAG, "EmptyView is null!")
        }
        if (null == mSucceedView) {
            mSucceedView = createSuccessView()
        }
        if (null != mSucceedView) {
            mSucceedView?.visibility =
                if (mState == LoadState.SUCCESS.stateValue()) VISIBLE else GONE
            if (mStateListener != null) {
                mStateListener?.onState(LoadState.SUCCESS)
            }
        } else {
            Log.i(TAG, "SuccessView is null!")
        }
        if (mStateListener != null) {
            if (loadState == LoadState.UNLOADED) {
                loadState = LoadState.LOADING
            }
            mStateListener?.onState(loadState)
        }
    }

    /**
     * 加载成功界面
     */
    protected fun createSuccessView(): View? {
        val childCount = childCount
        if (childCount > mMaxCount) {
            throw RuntimeException(javaClass.simpleName + " can host only one direct child")
        }
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view !== loadingView && view !== emptyView && view !== errorView) {
                return view
            }
        }
        return null
    }

    /**
     * 正在加载界面
     */
    protected abstract fun createLoadingView(): View?

    /**
     * 没有数据界面
     */
    protected abstract fun createEmptyView(): View?

    /**
     * 网络异常界面
     */
    protected abstract fun createErrorView(): View?

    /**
     * 重载页面
     */
    fun reload() {
        if (mRListener != null) {
            mState = LoadState.UNLOADED.stateValue()
            showSafePagerView()
            mRListener?.onReload()
        } else {
            Log.i(javaClass.simpleName, "OnReloadListener is null.")
        }
    }

    /**
     * 加载状态切换时触发
     */
    fun setOnStateListener(listener: OnStateListener?) {
        mStateListener = listener
    }

    /**
     * 网络请求失败，重新加载时触发
     */
    fun setOnReloadListener(listener: OnReloadListener?) {
        mRListener = listener
    }

    /**
     * 设置加载结果
     */
    fun setLoadState(loadState: LoadState): Loader {
        this.loadState = loadState
        if (mState == LoadState.ERROR.stateValue() || mState == LoadState.EMPTY.stateValue() || mState == LoadState.LOADING.stateValue()) {
            mState = LoadState.UNLOADED.stateValue()
        }
        if (mState == LoadState.UNLOADED.stateValue()) {
            mState = LoadState.LOADING.stateValue()
            //getHandler().post(new TaskRunnable());
            post { mHandler.sendEmptyMessage(1) }
        } else {
            mState = loadState.stateValue()
            showSafePagerView()
        }
        return this
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                mState = loadState?.stateValue() ?: LoadState.UNLOADED.stateValue()
                showPagerView()
            }
        }
    }

    companion object {
        private val TAG = Loader::class.java.simpleName
    }

    init {
        initState(attrs, defStyle)
        initView()
    }
}