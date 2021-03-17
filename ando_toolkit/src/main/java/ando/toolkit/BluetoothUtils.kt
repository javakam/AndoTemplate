package ando.toolkit

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.util.Log
import ando.toolkit.ext.ToastUtils.shortToast
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * # BluetoothUtils
 *
 * 蓝牙连接工具
 *
 * @author javakam
 * @date 2019/11/15 13:38
 */
@SuppressLint("MissingPermission")
class BluetoothUtils private constructor() {

    private val mAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var mScanListener: OnScanListener? = null
    private var mBondListener: OnBondListener? = null
    private var mOpenListener: OnOpenListener? = null

    interface OnBondListener {
        /**
         * 已经配对
         */
        fun onBond(device: BluetoothDevice?)
    }

    interface OnScanListener {
        /**
         * 扫描蓝牙
         */
        fun onFind(device: BluetoothDevice?)
    }

    interface OnOpenListener {
        /**
         * 蓝牙是否开启
         */
        fun onOpen(isOpen: Boolean)
    }

    /**
     * 扫描监听
     */
    fun setOnScanListener(listener: OnScanListener?) {
        mScanListener = listener
    }

    /**
     * 配对监听
     */
    fun setOnBondListener(listener: OnBondListener?) {
        mBondListener = listener
    }

    /**
     * 开关监听
     */
    fun setOnOpenListener(listener: OnOpenListener?) {
        mOpenListener = listener
    }

    /**
     * 判断蓝牙是否开启
     */
    val isOpen: Boolean
        get() {
            if (mAdapter == null) {
                shortToast("此设备不支持蓝牙传输功能！")
                return false
            }
            return mAdapter.isEnabled
        }

    /**
     * 开启蓝牙
     */
    fun open(open: Boolean) {
        if (open) {
            mAdapter?.enable()
        } else {
            mAdapter?.disable()
        }
    }

    /**
     * 获取已经配对的蓝牙设备
     */
    val bondedDevices: ArrayList<BluetoothDevice>
        get() {
            val devices = mAdapter?.bondedDevices
            val list = ArrayList<BluetoothDevice>()
            devices?.let { list.addAll(it) }
            return list
        }

    /**
     * 扫描蓝牙设备
     */
    fun startScan(): Boolean = mAdapter?.startDiscovery() ?: false

    /**
     * 取消蓝牙扫描
     */
    fun stopScan(): Boolean = mAdapter?.cancelDiscovery() ?: false

    /**
     * 蓝牙配对
     */
    fun createBond(device: BluetoothDevice): Boolean {
        if (device.bondState == BluetoothDevice.BOND_NONE) {
            try {
                val createBondMethod = BluetoothDevice::class.java
                    .getMethod("createBond")
                return createBondMethod.invoke(device) as Boolean
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * 解除配对
     */
    fun removeBond(btDevice: BluetoothDevice?): Boolean {
        try {
            val removeBondMethod = BluetoothDevice::class.java
                .getMethod("removeBond")
            return removeBondMethod.invoke(btDevice) as Boolean
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 打印相关信息
     */
    fun printAllInform() {
        try {
            // 取得所有方法
            val hideMethod = BluetoothDevice::class.java.methods
            var i = 0
            while (i < hideMethod.size) {
                Log.e("method name", hideMethod[i].name)
                i++
            }
            // 取得所有常量
            val allFields = BluetoothDevice::class.java.fields
            i = 0
            while (i < allFields.size) {
                Log.e("Field name", allFields[i].name)
                i++
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        // 扫描判断
        if (BluetoothDevice.ACTION_FOUND == action) { // 每扫描到一个设备，系统都会发送此广播。
            if (mScanListener == null)  return
            // 获取蓝牙设备
            val device = intent
                .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            if (device != null) {
                mScanListener!!.onFind(device)
            }
        }

        // 配对判断
        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
            if (mBondListener == null) return
            val device = intent
                .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            mBondListener!!.onBond(device)
        }

        // 状态改变
        if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
            if (mOpenListener == null) return
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
            if (state == BluetoothAdapter.STATE_ON) { // 蓝牙开启
                mOpenListener!!.onOpen(true)
            } else if (state == BluetoothAdapter.STATE_OFF) { // 蓝牙关闭
                mOpenListener!!.onOpen(false)
            }
        }
    }

    companion object {
        private val instance = BluetoothUtils()
        fun init(): BluetoothUtils {
            return instance
        }
    }

}