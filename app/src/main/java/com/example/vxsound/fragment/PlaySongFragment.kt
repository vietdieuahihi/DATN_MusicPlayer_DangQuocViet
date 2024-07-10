package com.example.vxsound.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.vxsound.R
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.FragmentPlaySongBinding
import com.example.vxsound.model.Song
import com.example.vxsound.service.MusicService
import com.example.vxsound.utils.AppUtil
import com.example.vxsound.utils.GlideUtils
import java.util.Timer
import java.util.TimerTask

@SuppressLint("NonConstantResourceId")
class PlaySongFragment : Fragment(), View.OnClickListener {
    private var mFragmentPlaySongBinding: FragmentPlaySongBinding? = null
    private var mTimer: Timer? = null
    private var mAction = 0
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0)
            handleMusicAction()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentPlaySongBinding = FragmentPlaySongBinding.inflate(inflater, container, false)
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity!!).registerReceiver(
                mBroadcastReceiver,
                IntentFilter(Constant.CHANGE_LISTENER)
            )
        }
        initControl()
        showInfoSong()
        updateStatusShuffleButton()
        updateStatusRepeatButton()
        mAction = MusicService.mAction
        handleMusicAction()
        return mFragmentPlaySongBinding?.root
//////chay chu
//        mFragmentPlaySongBinding!!.tvSongName.isSelected = true
//        mFragmentPlaySongBinding!!.tvSongName.requestFocus()
//        mFragmentPlaySongBinding!!.tvArtist.isSelected = true
//        mFragmentPlaySongBinding!!.tvArtist.requestFocus()


//        mActivityMainBinding!!.layoutBottom.tvSongName.isSelected = true
//        mActivityMainBinding!!.layoutBottom.tvSongName.requestFocus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentPlaySongBinding!!.tvSongName.isSelected = true
        mFragmentPlaySongBinding!!.tvSongName.requestFocus()
        mFragmentPlaySongBinding!!.tvArtist.isSelected = true
        mFragmentPlaySongBinding!!.tvArtist.requestFocus()
    }

    private fun initControl() {
        mTimer = Timer()
        mFragmentPlaySongBinding?.imgShuffle?.setOnClickListener(this)
        mFragmentPlaySongBinding?.imgRepeat?.setOnClickListener(this)
        mFragmentPlaySongBinding?.imgPrevious?.setOnClickListener(this)
        mFragmentPlaySongBinding?.imgPlay?.setOnClickListener(this)
        mFragmentPlaySongBinding?.imgNext?.setOnClickListener(this)
        mFragmentPlaySongBinding?.seekbar?.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                MusicService.mPlayer!!.seekTo(seekBar.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
        })
    }

    private fun showInfoSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying!!.isEmpty()) {
            return
        }
        val currentSong: Song = MusicService.mListSongPlaying!![MusicService.mSongPosition]
        mFragmentPlaySongBinding?.tvSongName?.text = currentSong.title
        mFragmentPlaySongBinding?.tvArtist?.text = currentSong.artist
        GlideUtils.loadUrl(currentSong.image, mFragmentPlaySongBinding?.imgSong!!)
    }

    private fun handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            if (activity != null) {
                activity!!.onBackPressed()
            }
            return
        }
        when (mAction) {
            Constant.PREVIOUS, Constant.NEXT -> {
                stopAnimationPlayMusic()
                showInfoSong()
            }

            Constant.PLAY -> {
                showInfoSong()
                if (MusicService.isPlaying) {
                    startAnimationPlayMusic()
                }
                showSeekBar()
                showStatusButtonPlay()
            }

            Constant.PAUSE -> {
                stopAnimationPlayMusic()
                showSeekBar()
                showStatusButtonPlay()
            }

            Constant.RESUME -> {
                startAnimationPlayMusic()
                showSeekBar()
                showStatusButtonPlay()
            }
        }
    }

    private fun startAnimationPlayMusic() {
        val runnable: Runnable = object : Runnable {
            override fun run() {
                mFragmentPlaySongBinding?.imgSong?.animate()?.rotationBy(360f)?.withEndAction(this)
                    ?.setDuration(15000)
                    ?.setInterpolator(LinearInterpolator())?.start()
            }
        }
        mFragmentPlaySongBinding?.imgSong?.animate()?.rotationBy(360f)?.withEndAction(runnable)
            ?.setDuration(15000)
            ?.setInterpolator(LinearInterpolator())?.start()
    }

    private fun stopAnimationPlayMusic() {
        mFragmentPlaySongBinding?.imgSong?.animate()?.cancel()
    }

    private fun showSeekBar() {
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                if (activity == null) {
                    return
                }
                activity!!.runOnUiThread {
                    if (MusicService.mPlayer == null) {
                        return@runOnUiThread
                    }
                    mFragmentPlaySongBinding?.tvTimeCurrent?.text =
                        AppUtil.getTime(MusicService.mPlayer!!.currentPosition)
                    mFragmentPlaySongBinding?.tvTimeMax?.text =
                        AppUtil.getTime(MusicService.mLengthSong)
                    mFragmentPlaySongBinding?.seekbar?.max = MusicService.mLengthSong
                    mFragmentPlaySongBinding?.seekbar?.progress =
                        MusicService.mPlayer!!.currentPosition
                }
            }
        }, 0, 1000)
    }

    private fun showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            mFragmentPlaySongBinding?.imgPlay?.setImageResource(R.drawable.ic_pause_black)
        } else {
            mFragmentPlaySongBinding?.imgPlay?.setImageResource(R.drawable.ic_play_black)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mBroadcastReceiver)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_previous -> clickOnPrevButton()
            R.id.img_play -> clickOnPlayButton()
            R.id.img_next -> clickOnNextButton()
            R.id.img_shuffle -> clickOnShuffleButton()
            R.id.img_repeat -> clickOnRepeatButton()
            else -> {}
        }
    }

    private fun clickOnShuffleButton() {
        if (!MusicService.isShuffle) {
            MusicService.isShuffle = true
            MusicService.isRepeat = false
        } else {
            MusicService.isShuffle = false
        }
        updateStatusShuffleButton()
        updateStatusRepeatButton()
    }

    private fun clickOnRepeatButton() {
        if (!MusicService.isRepeat) {
            MusicService.isRepeat = true
            MusicService.isShuffle = false
        } else {
            MusicService.isRepeat = false
        }
        updateStatusShuffleButton()
        updateStatusRepeatButton()
    }

    private fun updateStatusShuffleButton() {
        if (MusicService.isShuffle) {
            mFragmentPlaySongBinding?.imgShuffle?.setImageResource(R.drawable.ic_shuffle_enable)
        } else {
            mFragmentPlaySongBinding?.imgShuffle?.setImageResource(R.drawable.ic_shuffle_disable)
        }
    }

    private fun updateStatusRepeatButton() {
        if (MusicService.isRepeat) {
            mFragmentPlaySongBinding?.imgRepeat?.setImageResource(R.drawable.ic_repeat_one_enable)
        } else {
            mFragmentPlaySongBinding?.imgRepeat?.setImageResource(R.drawable.ic_repeat_disable)
        }
    }

    private fun clickOnPrevButton() {
        GlobalFunction.startMusicService(activity, Constant.PREVIOUS, MusicService.mSongPosition)
    }

    private fun clickOnNextButton() {
        GlobalFunction.startMusicService(activity, Constant.NEXT, MusicService.mSongPosition)
    }

    private fun clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFunction.startMusicService(activity, Constant.PAUSE, MusicService.mSongPosition)
        } else {
            GlobalFunction.startMusicService(activity, Constant.RESUME, MusicService.mSongPosition)
        }
    }
}