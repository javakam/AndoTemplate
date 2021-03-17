package ando.library.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import java.lang.RuntimeException

/**
 * # BaseFragment
 *
 * @author javakam
 * @date 2019/3/17 13:24
 */

abstract class BaseFragment : Fragment(), IBackPressed {
    /**
     * 注:不要命名为 activity 会和 Fragment.getActivity 冲突
     */
    protected lateinit var baseActivity: BaseActivity
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseActivity = context as BaseActivity
    }

    override fun onBackPressed(): Boolean = false
}

abstract class BaseMvcFragment : BaseFragment(), IBaseInterface {
    protected lateinit var rootView: View
        private set

    private var isDataInitiated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = if (getLayoutId() > 0) {
            inflater.inflate(getLayoutId(), container, false)
        } else {
            getLayoutView() ?: throw RuntimeException(
                "getLayoutId is invalid and getLayoutView return value is null"
            )
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        initListener()
        if (this !is IFragmentLazyLoad) {
            initData()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isDataInitiated && this is IFragmentLazyLoad) {
            initData()
            isDataInitiated = true
        }
    }
}

abstract class BaseMvvmFragment<T : ViewDataBinding> : BaseFragment() {
    abstract val layoutId: Int
    lateinit var binding: T
    abstract fun initView(root: View, savedInstanceState: Bundle?)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        initView(binding.root, savedInstanceState)
        return binding.root
    }
}

abstract class BaseMvcLazyFragment : BaseMvcFragment(), IFragmentLazyLoad
