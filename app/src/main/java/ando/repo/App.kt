package ando.repo

import ando.file.core.FileOperator
import ando.library.utils.CrashHandler
import ando.toolkit.AppUtils
import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val debug = true //BuildConfig.DEBUG
        AppUtils.init(this, debug)
        FileOperator.init(this, debug)
        CrashHandler.init(this, "${externalCacheDir?.path}/Crash/")
    }

}