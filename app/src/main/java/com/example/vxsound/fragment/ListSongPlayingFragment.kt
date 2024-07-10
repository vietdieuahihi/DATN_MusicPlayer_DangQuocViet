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
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vxsound.adapter.SongPlayingAdapter
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.FragmentListSongPlayingBinding
import com.example.vxsound.listener.IOnClickSongPlayingItemListener
import com.example.vxsound.service.MusicService
import kotlinx.android.synthetic.main.item_song.view.tv_song_name

class ListSongPlayingFragment : Fragment() {
    private var mFragmentListSongPlayingBinding: FragmentListSongPlayingBinding? = null
    private var mSongPlayingAdapter: SongPlayingAdapter? = null
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateStatusListSongPlaying()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentListSongPlayingBinding =
            FragmentListSongPlayingBinding.inflate(inflater, container, false)
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity!!).registerReceiver(
                mBroadcastReceiver,
                IntentFilter(Constant.CHANGE_LISTENER)
            )
        }
        displayListSongPlaying()
        return mFragmentListSongPlayingBinding?.root

        //
        //Chay chu

    }

    private fun displayListSongPlaying() {
        if (activity == null || MusicService.mListSongPlaying == null) {
            return
        }
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentListSongPlayingBinding?.rcvData?.layoutManager = linearLayoutManager
        mSongPlayingAdapter = SongPlayingAdapter(MusicService.mListSongPlaying,
            object : IOnClickSongPlayingItemListener {
                override fun onClickItemSongPlaying(position: Int) {
                    clickItemSongPlaying(position)
                }

            })
        mFragmentListSongPlayingBinding?.rcvData?.adapter = mSongPlayingAdapter
        updateStatusListSongPlaying()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateStatusListSongPlaying() {
        if (activity == null || MusicService.mListSongPlaying == null || MusicService.mListSongPlaying!!.isEmpty()) {
            return
        }
        for (i in MusicService.mListSongPlaying!!.indices) {
            MusicService.mListSongPlaying!![i].isPlaying = (i == MusicService.mSongPosition)
        }
        mSongPlayingAdapter!!.notifyDataSetChanged()
    }

    private fun clickItemSongPlaying(position: Int) {
        MusicService.isPlaying = false
        GlobalFunction.startMusicService(activity, Constant.PLAY, position)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mBroadcastReceiver)
        }
    }
}