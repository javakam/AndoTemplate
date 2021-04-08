package ando.library.widget.float

import android.content.Context
import android.content.SharedPreferences

data class FloatPosition(var mDownX: Int = 0, var mDownY: Int = 0)

object UserSetting {

    private const val KEY_SP_FLOAT = "float_setting"
    private const val FLOAT_X = "floatX"
    private const val FLOAT_Y = "floatY"

    fun setFloatPosition(context: Context, x: Int, y: Int) {
        getSP(context).edit().putInt(FLOAT_X, x).putInt(FLOAT_Y, y)
            .apply()
    }

    fun getFloatPosition(context: Context): FloatPosition {
        return FloatPosition(
            getSP(context).getInt(FLOAT_X, 80),
            getSP(context).getInt(FLOAT_Y, 100)
        )
    }

    private fun getSP(context: Context): SharedPreferences {
        return context.getSharedPreferences(KEY_SP_FLOAT, Context.MODE_PRIVATE)
    }

}