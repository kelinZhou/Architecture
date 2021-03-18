package com.kelin.architecture.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.kelin.architecture.R
import java.io.File

/**
 * **描述:** 图片加载
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/12/11 9:54 PM
 *
 * **版本:** v 1.0.0
 */

/**
 * 加载头像。
 */

fun ImageView.fileAvatar(file: File, @DrawableRes defAvatar: Int = R.drawable.avatar_default) {
    if (!file.exists()) {
        setImageResource(defAvatar)
    } else {
        Glide.with(this).load(file)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions().transform(CircleCrop())
                    .placeholder(defAvatar)
                    .error(defAvatar)
            ).into(this)
    }
}

fun ImageView.netWorkAvatar(url: String?, @DrawableRes defAvatar: Int = R.drawable.avatar_default) {
    if (url.isNullOrEmpty()) {
        setImageResource(defAvatar)
    } else {
        if (getTag(0x1f00_1001) != url) {
            setTag(0x1f00_1001, url)
            Glide.with(this).load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(
                    RequestOptions().transform(CircleCrop())
                        .placeholder(defAvatar)
                        .error(defAvatar)
                ).into(this)
        }
    }
}

fun ImageView.fileImage(file: File, @DrawableRes placeholder: Int = 0, @DrawableRes error: Int = placeholder) {
    if (!file.exists()) {
        setImageResource(placeholder)
    } else {
        Glide.with(this).load(file)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions()
                    .placeholder(placeholder)
                    .error(error)
            ).into(this)
    }
}

fun ImageView.netWork(url: String?, @DrawableRes placeholder: Int = R.drawable.kelin_photo_selector_img_load_error, @DrawableRes error: Int = placeholder, cacheEnable: Boolean = true) {
    if (url.isNullOrEmpty()) {
        setImageResource(placeholder)
    } else {
        Glide.with(this).load(url)
            .transition(DrawableTransitionOptions.withCrossFade()).let {
                if (cacheEnable) {
                    it
                } else {
                    it.skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                }
            }
            .apply(
                RequestOptions()
                    .placeholder(placeholder)
                    .error(error)
            ).into(this)
    }
}

fun ImageView.loadImage(url: String, corners: Int = 0, onResult: (bitmap: Bitmap?) -> Unit) {
    Glide.with(this)
        .asBitmap()
        .load(url)
        .let {
            if (corners > 0) {
                it.apply(RequestOptions().transform(RoundedCorners(corners)))
            } else {
                it
            }
        }
        .into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                onResult(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                onResult(null)
            }
        })
}