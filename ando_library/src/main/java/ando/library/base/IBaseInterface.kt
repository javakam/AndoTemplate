package ando.library.base

import android.os.Bundle
import android.view.View

interface IBaseInterface {
    fun initView(savedInstanceState: Bundle?)
    fun initListener() {}
    fun initData() {}

    fun getLayoutView(): View? = null
    fun getLayoutId(): Int = -1
}

interface IFragmentLazyLoad

interface IBackPressed {
    fun onBackPressed(): Boolean
}