package com.example.vxsound.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.vxsound.R

object GlideUtils {
    fun loadUrlBanner(url: String?, imageView: ImageView) {
        if (StringUtil.isEmpty(url)) {
            imageView.setImageResource(R.drawable.img_no_image)
            return
        }
        try {
            Glide.with(imageView.context)
                .load(url)
                .error(R.drawable.img_no_image)
                .dontAnimate()
                .into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadUrl(url: String?, imageView: ImageView) {
        if (StringUtil.isEmpty(url)) {
            imageView.setImageResource(R.drawable.image_no_available)
            return
        }
        try {
            Glide.with(imageView.context)
                .load(url)
                .error(R.drawable.image_no_available)
                .dontAnimate()
                .into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}