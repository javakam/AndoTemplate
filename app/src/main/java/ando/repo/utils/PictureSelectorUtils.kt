package ando.repo.utils

import ando.repo.R
import ando.toolkit.AppUtils
import ando.toolkit.ext.noNull
import ando.toolkit.ext.toastShort
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ando.repo.utils.PermissionUtils.REQUEST_CODE_CAMERA
import ando.repo.utils.PermissionUtils.REQUEST_CODE_STORAGE
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.style.PictureCropParameterStyle
import com.luck.picture.lib.style.PictureSelectorUIStyle
import com.luck.picture.lib.tools.PictureFileUtils
import com.luck.picture.lib.tools.ScreenUtils
import java.io.File

/**
 * # PictureSelectorHelper
 *
 * 照片选择器辅助类
 *
 * <pre>
 *     https://github.com/LuckSiege/PictureSelector
 *
 *     https://github.com/LuckSiege/PictureSelector/blob/master/app/src/main/java/com/luck/pictureselector/MainActivity.java
 * </pre>
 *
 * @author javakam
 * @date 2020/1/14  13:23
 */
class PictureSelectorUtils(private val context: Context) {

    private val pictureSelector: PictureSelector =
        when (context) {
            is Activity -> PictureSelector.create(context)
            is Fragment -> PictureSelector.create(context)
            else        -> throw RuntimeException("Context is neither Activity nor Fragment!")
        }

    private fun takePicture() {
        //val uiStyle = PictureSelectorUIStyle.ofDefaultStyle()
        pictureSelector.openCamera(PictureMimeType.ofImage())
            .theme(R.style.pictureBlueStyle)
            .selectionMode(PictureConfig.SINGLE)
            .isPreviewImage(false)
            .isPreviewVideo(false)

            .isZoomAnim(true)
            .isEnableCrop(false)
            .freeStyleCropEnabled(false)
            .circleDimmedLayer(true)
            .showCropFrame(false)
            .showCropGrid(false)
            .scaleEnabled(true)
            .isDragFrame(false)
            .imageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
            //.setPictureStyle(R.style.picture_default_style)// 动态自定义相册主题
            //.setPictureCropStyle(getCropStyle())// 动态自定义裁剪主题
            .setPictureUIStyle(geNumberStyle())

            .isCompress(false)// 是否压缩
            .compressQuality(100)// 图片压缩后输出质量 0 ~ 100
            .synOrAsy(false)//同步true或异步false 压缩 默认同步
            //.queryMaxFileSize(500.0F)// 只查多少M以内的图片、视频、音频  单位M
            //.compressSavePath(getPath())//压缩图片保存地址
            .cutOutQuality(100)// 裁剪输出质量 默认 100
            .minimumCompressSize(100)// 小于100kb的图片不压缩
            .imageEngine(PictureSelectorEngine)// 外部传入图片加载引擎，必传项
            //.forResult(PictureConfig.REQUEST_CAMERA)
            .forResult(onResultCallbackListener)
    }

    private fun selectPicture() {
        //PictureSelectorUIStyle.ofSelectNumberStyle()
        pictureSelector
            .openGallery(PictureMimeType.ofImage())
            .selectionMode(PictureConfig.SINGLE)
            .setPictureUIStyle(geNumberStyle())
            //.setPictureCropStyle(getCropStyle())  //动态自定义裁剪主题
            //.setPictureStyle(getNumStyle())       //动态自定义相册主题
            //.theme(R.style.pictureBlueStyle)
            .isCamera(true)
            .isPreviewImage(false)
            .isPreviewVideo(false)
            .isZoomAnim(true)
            .isEnableCrop(false)
            .isDragFrame(false)
            .freeStyleCropEnabled(false)
            .circleDimmedLayer(true)
            .showCropFrame(false)
            .showCropGrid(false)
            .scaleEnabled(true)
            .imageFormat(PictureMimeType.JPEG)
            .isCompress(false)
            .compressQuality(100)
            .synOrAsy(false)
            //.queryMaxFileSize(10)         //只查多少M以内的图片、视频、音频  单位M
            //.compressSavePath(getPath())  //压缩图片保存地址
            .cutOutQuality(100)
            .minimumCompressSize(100)
            .imageEngine(PictureSelectorEngine)
            //.forResult(PictureConfig.CHOOSE_REQUEST)
            .forResult(onResultCallbackListener)
    }

    private val onResultCallbackListener = object : OnResultCallbackListener<LocalMedia> {
        override fun onStart(activity: Activity) {
            callback?.onStart(activity)
        }

        override fun onResult(result: MutableList<LocalMedia>?) {
            handleResult(result)
        }

        override fun onCancel() {
        }
    }

    /**
     * 裁剪主题
     */
    private fun getCropStyle(): PictureCropParameterStyle {
        return PictureCropParameterStyle(
            ContextCompat.getColor(context, R.color.app_color_blue),
            ContextCompat.getColor(context, R.color.app_color_blue),
            ContextCompat.getColor(context, R.color.app_color_blue),
            ContextCompat.getColor(context, R.color.app_color_blue),
            true
        )
    }

    /**
     * 数字风格样式
     */
    private fun geNumberStyle(): PictureSelectorUIStyle {
        val uiStyle = PictureSelectorUIStyle()
        val colorBlue = ContextCompat.getColor(context, R.color.app_color_blue)
        val colorWhite = ContextCompat.getColor(context, R.color.app_color_white)

        uiStyle.picture_statusBarBackgroundColor = colorBlue
        uiStyle.picture_container_backgroundColor = colorWhite
        uiStyle.picture_switchSelectNumberStyle = true
        uiStyle.picture_navBarColor = colorBlue
        uiStyle.picture_check_style = R.drawable.picture_checkbox_num_selector
        uiStyle.picture_top_leftBack = R.drawable.ic_close //picture_icon_back
        uiStyle.picture_top_titleRightTextColor = intArrayOf(colorWhite, colorWhite)
        uiStyle.picture_top_titleRightTextSize = 14
        uiStyle.picture_top_titleTextSize = 18
        uiStyle.picture_top_titleArrowUpDrawable = R.drawable.picture_icon_arrow_up
        uiStyle.picture_top_titleArrowDownDrawable = R.drawable.picture_icon_arrow_down
        uiStyle.picture_top_titleTextColor = colorWhite
        uiStyle.picture_top_titleBarBackgroundColor = colorBlue
        uiStyle.picture_album_textSize = 16
        uiStyle.picture_album_backgroundStyle = R.drawable.picture_item_select_bg
        uiStyle.picture_album_textColor = Color.parseColor("#4d4d4d")
        uiStyle.picture_album_checkDotStyle = R.drawable.picture_num_oval_blue
        uiStyle.picture_bottom_previewTextSize = 14
        uiStyle.picture_bottom_previewTextColor =
            intArrayOf(Color.parseColor("#9b9b9b"), colorBlue)
        uiStyle.picture_bottom_completeRedDotTextSize = 12
        uiStyle.picture_bottom_completeTextSize = 14
        uiStyle.picture_bottom_completeRedDotTextColor = colorWhite
        uiStyle.picture_bottom_completeRedDotBackground = R.drawable.picture_num_oval_blue
        uiStyle.picture_bottom_completeTextColor = intArrayOf(Color.BLACK, colorBlue)
        uiStyle.picture_bottom_barBackgroundColor = colorWhite
        uiStyle.picture_adapter_item_camera_backgroundColor = Color.parseColor("#999999")
        uiStyle.picture_adapter_item_camera_textColor = colorWhite
        uiStyle.picture_adapter_item_camera_textSize = 14
        uiStyle.picture_adapter_item_camera_textTopDrawable = R.drawable.picture_icon_camera
        uiStyle.picture_adapter_item_textSize = 12
        uiStyle.picture_adapter_item_textColor = colorWhite
        uiStyle.picture_adapter_item_video_textLeftDrawable = R.drawable.picture_icon_video
        uiStyle.picture_adapter_item_audio_textLeftDrawable = R.drawable.picture_icon_audio
        uiStyle.picture_bottom_originalPictureTextSize = 14
        uiStyle.picture_bottom_originalPictureCheckStyle = R.drawable.picture_original_blue_checkbox
        uiStyle.picture_bottom_originalPictureTextColor = colorBlue
        uiStyle.picture_bottom_previewNormalText = R.string.picture_preview_num
        uiStyle.picture_bottom_originalPictureText = R.string.picture_original_image
        uiStyle.picture_bottom_completeDefaultText = R.string.picture_please_select
        uiStyle.picture_bottom_completeNormalText = R.string.picture_completed
        uiStyle.picture_adapter_item_camera_text = R.string.picture_take_picture
        uiStyle.picture_top_titleRightDefaultText = R.string.picture_cancel
        uiStyle.picture_top_titleRightNormalText = R.string.picture_cancel
        uiStyle.picture_bottom_previewDefaultText = R.string.picture_preview

        uiStyle.picture_top_titleBarHeight = ScreenUtils.dip2px(context, 48f)
        uiStyle.picture_bottom_barHeight = ScreenUtils.dip2px(context, 45f)
        // 如果文本内容设置(%1$d/%2$d)，请开启true
        uiStyle.isCompleteReplaceNum = true
        return uiStyle
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        //目前没用到
        //清理头像缓存后再弹窗 , Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (REQUEST_CODE_STORAGE == requestCode) {
            if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
                if (PermissionUtils.havePermissions(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    clearCache()
                } else {
                    if (context is Activity) {
                        PermissionUtils.verifyStoragePermissions(context)
                    } else if (context is Fragment) {
                        PermissionUtils.verifyStoragePermissions(context.requireActivity())
                    }
                }
            } else {
                shortToast(R.string.picture_jurisdiction)
            }
            return
        }

        //拍照
        if (REQUEST_CODE_CAMERA == requestCode) {
            val isCamera = Manifest.permission.CAMERA == permissions[0].noNull()
            val isStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE == permissions[1].noNull()

            if (isCamera) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    shortToast(R.string.picture_camera)
                    return  //主要的权限都没了,存储权限的吐司就不要了
                }
            }
            if (isStorage) {
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    PictureFileUtils.deleteCacheDirFile(context, PictureMimeType.ofImage())
                } else {
                    shortToast(R.string.picture_jurisdiction)
                    return  //缺少存储权限,也不能拍照
                }
            }
            //Manifest.permission.CAMERA && Manifest.permission.WRITE_EXTERNAL_STORAGE
            takePicture()
        }
    }

    fun onActivityResult(requestCode: Int, data: Intent) {
        if (requestCode == PictureConfig.CHOOSE_REQUEST || requestCode == PictureConfig.REQUEST_CAMERA) {
            // 图片选择结果回调
            val selectList: List<LocalMedia>? = PictureSelector.obtainMultipleResult(data)
            handleResult(selectList)
        }
    }

    private fun handleResult(selectList: List<LocalMedia>?) {
        if (selectList.isNullOrEmpty()) return

        // 例如 LocalMedia 里面返回五种path
        // 1.media.getPath(); 为原图path
        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
        // 4.media.getOriginalPath()); media.isOriginal());为true时此字段才有值
        // 5.media.getAndroidQToPath();为Android Q版本特有返回的字段，此字段有值就用来做上传使用
        // 如果同时开启裁剪和压缩，则取压缩路径为准因为是先裁剪后压缩
        if (!selectList.isNullOrEmpty()) {
            for (media in selectList) {
                print("是否压缩:" + media.isCompressed)
                print("压缩:" + media.compressPath)
                print("原图:" + media.path)
                print("绝对路径:" + media.realPath)
                print("是否裁剪:" + media.isCut)
                print("裁剪:" + media.cutPath)
                print("是否开启原图:" + media.isOriginal)
                print("原图路径:" + media.originalPath)
                print("Android Q 特有Path:" + media.androidQToPath)
                print("宽高: " + media.width + "x" + media.height)
                print("Size: " + media.size)
            }
        }

        val media = selectList[0]
        var path: String = if (media.isCut && !media.isCompressed) {
            // 裁剪过
            media.cutPath
        } else if (media.isCompressed || media.isCut && media.isCompressed) {
            // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
            media.compressPath
        } else {
            // 原图
            media.path
        }
        path = if (path.startsWith("content://") && !media.isCut && !media.isCompressed)
            Uri.parse(path).toString() else path
        print("头像路径 : $path")
        print("原图地址::" + media.path)

        if (media.isCut) {
            print("裁剪地址::" + media.cutPath)
        }
        if (media.isCompressed) {
            print("压缩地址::" + media.compressPath)
            print("压缩后文件大小::" + File(media.compressPath).length() / 1024 + "k")
        }
        if (!TextUtils.isEmpty(media.androidQToPath)) {
            print("Android Q特有地址::" + media.androidQToPath)
        }
        if (media.isOriginal) {
            print("是否开启原图功能::" + true)
            print("开启原图功能后地址::" + media.originalPath)
        }
        if (callback != null) {
            callback?.onPicturePicked(path)
        }
    }

    /**
     * 拍照需要的权限:
     *
     * ```
     * Manifest.permission.CAMERA
     * Manifest.permission.READ_EXTERNAL_STORAGE
     * Manifest.permission.WRITE_EXTERNAL_STORAGE
     * ```
     */
    fun checkCameraPermission() {
        if (!PermissionUtils.havePermissions(context, *PermissionUtils.PERMISSIONS_CAMERA)) {
            if (context is Activity) {
                PermissionUtils.verifyCameraPermissions(context)
            } else if (context is Fragment) {
                PermissionUtils.verifyCameraPermissions(context.requireActivity())
            }
        } else {
            selectPicture()
        }
    }

    private fun clearCache() {
        PictureFileUtils.deleteCacheDirFile(context, PictureMimeType.ofImage())
        PictureFileUtils.deleteAllCacheDirFile(context)
    }

    private fun shortToast(text: Int) = context.toastShort(text)

    private fun print(s: String?) {
        if (AppUtils.isDebug()) {
            Log.i("123", s.noNull())
        }
    }

    interface CallBack {
        fun onStart(activity: Activity)
        fun onPicturePicked(path: String)
    }

    private var callback: CallBack? = null

    fun setCallBack(callback: CallBack) {
        this.callback = callback
    }

}