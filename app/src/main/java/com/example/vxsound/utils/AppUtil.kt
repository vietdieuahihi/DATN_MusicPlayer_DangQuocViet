package com.example.vxsound.utils

import android.annotation.SuppressLint

object AppUtil {
    @SuppressLint("DefaultLocale")
    fun getTime(millis: Int): String {
        val second = (millis / 1000 % 60).toLong()
        val minute = (millis / (1000 * 60)).toLong()
        return String.format("%02d:%02d", minute, second)
    }
}