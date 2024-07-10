package com.example.vxsound.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.vxsound.MyApplication
import com.example.vxsound.R
import com.example.vxsound.activity.AdminMainActivity
import com.example.vxsound.activity.MainActivity
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.model.Song
import com.example.vxsound.prefs.DataStoreManager
import com.example.vxsound.utils.StringUtil
import java.util.Random

class MusicService : Service(), OnPreparedListener, OnCompletionListener {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (mPlayer == null) {
            mPlayer = MediaPlayer()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey(Constant.MUSIC_ACTION)) {
                mAction = bundle.getInt(Constant.MUSIC_ACTION)
            }
            if (bundle.containsKey(Constant.SONG_POSITION)) {
                mSongPosition = bundle.getInt(Constant.SONG_POSITION)
            }
            handleActionMusic(mAction)
        }
        return START_NOT_STICKY
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            Constant.PLAY -> playSong()
            Constant.PREVIOUS -> prevSong()
            Constant.NEXT -> nextSong()
            Constant.PAUSE -> pauseSong()
            Constant.RESUME -> resumeSong()
            Constant.CANNEL_NOTIFICATION -> cancelNotification()
            else -> {}
        }
    }

    private fun playSong() {
        val songUrl = mListSongPlaying!![mSongPosition].url
        if (!StringUtil.isEmpty(songUrl)) {
            playMediaPlayer(songUrl)
        }
    }

    private fun pauseSong() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer!!.pause()
            isPlaying = false
            sendMusicNotification()
            sendBroadcastChangeListener()
        }
    }

    private fun cancelNotification() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer!!.pause()
            isPlaying = false
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        sendBroadcastChangeListener()
        stopSelf()
    }

    private fun resumeSong() {
        if (mPlayer != null) {
            mPlayer!!.start()
            isPlaying = true
            sendMusicNotification()
            sendBroadcastChangeListener()
        }
    }

    private fun prevSong() {
        val newPosition: Int = if (mListSongPlaying!!.size > 1) {
            if (isShuffle) {
                Random().nextInt(mListSongPlaying!!.size)
            } else {
                if (isRepeat) mSongPosition else if (mSongPosition > 0) {
                    mSongPosition - 1
                } else {
                    mListSongPlaying!!.size - 1
                }
            }
        } else {
            0
        }
        mSongPosition = newPosition
        sendMusicNotification()
        sendBroadcastChangeListener()
        playSong()
    }

    private fun nextSong() {
        val newPosition: Int = if (mListSongPlaying!!.size > 1) {
            if (isShuffle) {
                Random().nextInt(mListSongPlaying!!.size)
            } else {
                if (isRepeat) mSongPosition else if (mSongPosition < mListSongPlaying!!.size - 1) {
                    mSongPosition + 1
                } else {
                    0
                }
            }
        } else {
            0
        }
        mSongPosition = newPosition
        sendMusicNotification()
        sendBroadcastChangeListener()
        playSong()
    }

    private fun playMediaPlayer(songUrl: String?) {
        try {
            if (mPlayer!!.isPlaying) {
                mPlayer!!.stop()
            }
            mPlayer!!.reset()
            mPlayer!!.setDataSource(songUrl)
            mPlayer!!.prepareAsync()
            initControl()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initControl() {
        mPlayer!!.setOnPreparedListener(this)
        mPlayer!!.setOnCompletionListener(this)
    }

    private fun sendMusicNotification() {
        val song = mListSongPlaying!![mSongPosition]
        val pendingFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val intent: Intent = if (DataStoreManager.user?.isAdmin == true) {
            Intent(this, AdminMainActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        @SuppressLint("UnspecifiedImmutableFlag") val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, pendingFlag)
        val remoteViews = RemoteViews(packageName, R.layout.layout_push_notification_music)
        remoteViews.setTextViewText(R.id.tv_song_name, song.title)

        // Set listener
        remoteViews.setOnClickPendingIntent(
            R.id.img_previous,
            GlobalFunction.openMusicReceiver(this, Constant.PREVIOUS)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.img_next,
            GlobalFunction.openMusicReceiver(this, Constant.NEXT)
        )
        if (isPlaying) {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_pause_gray)
            remoteViews.setOnClickPendingIntent(
                R.id.img_play,
                GlobalFunction.openMusicReceiver(this, Constant.PAUSE)
            )
        } else {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_play_gray)
            remoteViews.setOnClickPendingIntent(
                R.id.img_play,
                GlobalFunction.openMusicReceiver(this, Constant.RESUME)
            )
        }
        remoteViews.setOnClickPendingIntent(
            R.id.img_close,
            GlobalFunction.openMusicReceiver(this, Constant.CANNEL_NOTIFICATION)
        )
        val builder = NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_small_push_notification)
            .setContentIntent(pendingIntent)
            .setSound(null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            builder.setCustomBigContentView(remoteViews)
        } else {
            builder.setCustomContentView(remoteViews)
        }
        val notification = builder.build()
        startForeground(1, notification)
    }

    override fun onCompletion(mp: MediaPlayer) {
        mAction = Constant.NEXT
        nextSong()
    }

    override fun onPrepared(mp: MediaPlayer) {
        mLengthSong = mPlayer!!.duration
        mp.start()
        isPlaying = true
        mAction = Constant.PLAY
        sendMusicNotification()
        sendBroadcastChangeListener()
        changeCountViewSong()
    }

    private fun sendBroadcastChangeListener() {
        val intent = Intent(Constant.CHANGE_LISTENER)
        intent.putExtra(Constant.MUSIC_ACTION, mAction)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun changeCountViewSong() {
        val songId = mListSongPlaying!![mSongPosition].id
        MyApplication[this].countViewDatabaseReference(songId)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentCount = snapshot.getValue(Int::class.java)
                    if (currentCount != null) {
                        val newCount = currentCount + 1
                        MyApplication[this@MusicService].countViewDatabaseReference(songId)
                            ?.removeEventListener(this)
                        MyApplication[this@MusicService].countViewDatabaseReference(songId)
                            ?.setValue(newCount)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayer != null) {
            mPlayer!!.release()
            mPlayer = null
        }
    }

    companion object {
        var isPlaying = false
        var mListSongPlaying: MutableList<Song>? = null
        var mSongPosition = 0
        var mPlayer: MediaPlayer? = null
        var mLengthSong = 0
        var mAction = -1
        var isShuffle = false
        var isRepeat = false
        fun clearListSongPlaying() {
            if (mListSongPlaying != null) {
                mListSongPlaying!!.clear()
            } else {
                mListSongPlaying = ArrayList()
            }
        }
    }
}