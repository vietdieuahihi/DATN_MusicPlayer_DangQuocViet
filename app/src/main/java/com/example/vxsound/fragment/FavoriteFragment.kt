package com.example.vxsound.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.vxsound.MyApplication
import com.example.vxsound.R
import com.example.vxsound.activity.PlayMusicActivity
import com.example.vxsound.adapter.SongAdapter
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.FragmentFavoriteBinding
import com.example.vxsound.listener.IOnClickSongItemListener
import com.example.vxsound.model.Song
import com.example.vxsound.service.MusicService
import com.example.vxsound.utils.GlideUtils

class FavoriteFragment : Fragment() {
    private var mFragmentFavoriteBinding: FragmentFavoriteBinding? = null
    private var mListSong: MutableList<Song>? = null
    private var mSongAdapter: SongAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mFragmentFavoriteBinding = FragmentFavoriteBinding.inflate(inflater, container, false)
        initUi()
        initListener()
        loadListFavoriteSongs()
        return mFragmentFavoriteBinding?.root
    }

    private fun initUi() {
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentFavoriteBinding?.rcvData?.layoutManager = linearLayoutManager
        mListSong = ArrayList()
        mSongAdapter = SongAdapter(mListSong, object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }

            override fun onClickFavoriteSong(song: Song, favorite: Boolean) {
                GlobalFunction.onClickFavoriteSong(activity, song, favorite)
            }
        })
        mFragmentFavoriteBinding?.rcvData?.adapter = mSongAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadListFavoriteSongs() {
        if (activity == null) return
        MyApplication[activity].songsDatabaseReference()
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        resetListData()
                        for (dataSnapshot in snapshot.children) {
                            val song = dataSnapshot.getValue(Song::class.java) ?: return
                            if (GlobalFunction.isFavoriteSong(song)) {
                                mListSong!!.add(0, song)
                            }
                        }
                        val isHasData = mListSong != null && mListSong!!.size > 1
                        displayLayoutPlayAll(isHasData)
                        if (mSongAdapter != null) mSongAdapter!!.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        GlobalFunction.showToastMessage(activity, getString(R.string.msg_get_date_error))
                    }
                })
    }

    private fun resetListData() {
        if (mListSong == null) {
            mListSong = ArrayList()
        } else {
            mListSong!!.clear()
        }
    }

    private fun goToSongDetail(song: Song) {
        MusicService.clearListSongPlaying()
        MusicService.mListSongPlaying!!.add(song)
        MusicService.isPlaying = false
        GlobalFunction.startMusicService(activity, Constant.PLAY, 0)
        GlobalFunction.startActivity(activity, PlayMusicActivity::class.java)
    }

    private fun initListener() {
        mFragmentFavoriteBinding?.layoutPlayAll?.setOnClickListener {
            if (mListSong == null || mListSong!!.isEmpty()) return@setOnClickListener
            MusicService.clearListSongPlaying()
            MusicService.mListSongPlaying!!.addAll(mListSong!!)
            MusicService.isPlaying = false
            GlobalFunction.startMusicService(activity, Constant.PLAY, 0)
            GlobalFunction.startActivity(activity, PlayMusicActivity::class.java)
        }
    }

    private fun displayLayoutPlayAll(isShow: Boolean) {
        if (isShow) {
            mFragmentFavoriteBinding?.layoutPlayAll?.visibility = View.VISIBLE
            GlideUtils.loadUrl(mListSong!![0].image, mFragmentFavoriteBinding?.imgPlayAll!!)
        } else {
            mFragmentFavoriteBinding?.layoutPlayAll?.visibility = View.GONE
        }
    }
}