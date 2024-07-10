package com.example.vxsound.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.vxsound.MyApplication
import com.example.vxsound.R
import com.example.vxsound.adapter.SongAdapter
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.ActivityFeaturedSongsBinding
import com.example.vxsound.listener.IOnClickSongItemListener
import com.example.vxsound.model.Song
import com.example.vxsound.service.MusicService
import com.example.vxsound.utils.GlideUtils

class FeaturedSongsActivity : BaseActivity() {

    private var mActivityFeaturedSongsBinding: ActivityFeaturedSongsBinding? = null
    private var mListSong: MutableList<Song>? = null
    private var mSongAdapter: SongAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityFeaturedSongsBinding = ActivityFeaturedSongsBinding.inflate(layoutInflater)
        setContentView(mActivityFeaturedSongsBinding?.root)
        setupToolbar()
        initUi()
        initListener()
        loadListFeaturedSongs()
    }

    private fun setupToolbar() {
        mActivityFeaturedSongsBinding?.header?.imgBack?.visibility = View.VISIBLE
        mActivityFeaturedSongsBinding?.header?.tvTitle?.text =
            getString(R.string.menu_featured_songs)
        mActivityFeaturedSongsBinding?.header?.imgBack?.setOnClickListener { onBackPressed() }
    }

    private fun initUi() {
        val linearLayoutManager = LinearLayoutManager(this)
        mActivityFeaturedSongsBinding?.rcvData?.layoutManager = linearLayoutManager
        mListSong = ArrayList()
        mSongAdapter = SongAdapter(mListSong, object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }

            override fun onClickFavoriteSong(song: Song, favorite: Boolean) {
                GlobalFunction.onClickFavoriteSong(this@FeaturedSongsActivity, song, favorite)
            }
        })
        mActivityFeaturedSongsBinding?.rcvData?.adapter = mSongAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadListFeaturedSongs() {
        MyApplication[this].songsDatabaseReference()
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    resetListData()
                    for (dataSnapshot in snapshot.children) {
                        val song = dataSnapshot.getValue(Song::class.java) ?: return
                        if (song.isFeatured == true) {
                            mListSong!!.add(0, song)
                        }
                    }
                    val isHasData = mListSong != null && mListSong!!.size > 1
                    displayLayoutPlayAll(isHasData)
                    if (mSongAdapter != null) mSongAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    GlobalFunction.showToastMessage(
                        this@FeaturedSongsActivity,
                        getString(R.string.msg_get_date_error)
                    )
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
        GlobalFunction.startMusicService(this, Constant.PLAY, 0)
        GlobalFunction.startActivity(this, PlayMusicActivity::class.java)
    }

    private fun initListener() {
        mActivityFeaturedSongsBinding?.layoutPlayAll?.setOnClickListener {
            if (mListSong == null || mListSong!!.isEmpty()) return@setOnClickListener
            MusicService.clearListSongPlaying()
            MusicService.mListSongPlaying!!.addAll(mListSong!!)
            MusicService.isPlaying = false
            GlobalFunction.startMusicService(this, Constant.PLAY, 0)
            GlobalFunction.startActivity(this, PlayMusicActivity::class.java)
        }
    }

    private fun displayLayoutPlayAll(isShow: Boolean) {
        if (isShow) {
            mActivityFeaturedSongsBinding?.layoutPlayAll?.visibility = View.VISIBLE
            GlideUtils.loadUrl(mListSong!![0].image, mActivityFeaturedSongsBinding?.imgPlayAll!!)
        } else {
            mActivityFeaturedSongsBinding?.layoutPlayAll?.visibility = View.GONE
        }
    }
}