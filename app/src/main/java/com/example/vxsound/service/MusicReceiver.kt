package com.example.vxsound.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction

class MusicReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.extras!!.getInt(Constant.MUSIC_ACTION)
        GlobalFunction.startMusicService(context, action, MusicService.mSongPosition)
    }
}