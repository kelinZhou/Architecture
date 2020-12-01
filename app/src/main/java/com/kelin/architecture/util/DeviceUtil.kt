package com.kelin.architecture.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.telephony.TelephonyManager
import com.kelin.architecture.util.sp.SpUtil
import java.util.*

/**
 * **描述:** 设备相关的工具类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/4/16  10:25 AM
 *
 * **版本:** v 1.0.0
 */
object DeviceUtil {
    private const val KEY_DEVICE_ID = "key_device_id"

    @SuppressLint("HardwareIds", "MissingPermission")
    fun getDeviceId(context: Context): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val deviceId = try {  //不需要每次获取都检测权限，所以在Splash页面申请权限通过后就保存到了本地，以后如果没有了权限的话就直接取本地的。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tm.imei
            } else {
                tm.deviceId
            }
        } catch (e: Exception) {
            SpUtil.getString(context, KEY_DEVICE_ID)
        }
        return if (deviceId.isEmpty()) {
            val uuid = UUID.randomUUID().toString()
            SpUtil.putString(context, KEY_DEVICE_ID, uuid)
            uuid
        } else {
            SpUtil.putString(context, KEY_DEVICE_ID, deviceId)
            deviceId
        }
    }


    /**
     * 获取当前的版本号。
     *
     * @param context 需要一个上下文。
     * @return 返回当前的版本号。
     */
    fun getAppVersionCode(context: Context) = try {
        context.packageManager.getPackageInfo(context.packageName, 0)?.versionCode ?: 0
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        0
    }

    /**
     * 获取当前的版本名称。
     *
     * @param context 需要一个上下文。
     * @return 返回当前的版本名称。
     */
    fun getAppVersionName(context: Context) = try {
        context.packageManager.getPackageInfo(context.packageName, 0)?.versionName ?: "未知版本"
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        "未知版本"
    }

    fun getScreenHeight(context: Context, percent:Double = 1.0): Int {
        return (context.resources.displayMetrics.heightPixels * percent).toInt()
    }

    fun getScreenWidth(context: Context, percent:Double = 1.0): Int {
        return (context.resources.displayMetrics.widthPixels * percent).toInt()
    }

    fun isSdCardAvailable(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

    /**
     *  * 根据uri获取真文件路径
     *
     * @param  context
     * @param  contentUri
     * @ return
     */
    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        val cursor = context.contentResolver.query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val realPath = cursor.getString(index)
            cursor.close()
            realPath
        }
    }

    /**
     * 判断指定包名的应用是否已经安装。
     * @param mContext 上下文。
     * @param packageName 要判断的包名。
     * @return 如果安装了返回true，否则返回false。
     */
    fun isPackageInstalled(mContext: Context, packageName: String): Boolean {
        var packageInfo: PackageInfo?
        try {
            packageInfo = mContext.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            packageInfo = null
            e.printStackTrace()
        }

        return packageInfo != null
    }
}