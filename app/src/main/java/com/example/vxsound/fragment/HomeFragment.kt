package com.example.vxsound.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.vxsound.MyApplication
import com.example.vxsound.R
import com.example.vxsound.activity.FeaturedSongsActivity
import com.example.vxsound.activity.NewSongsActivity
import com.example.vxsound.activity.PlayMusicActivity
import com.example.vxsound.activity.PopularSongsActivity
import com.example.vxsound.adapter.BannerSongAdapter
import com.example.vxsound.adapter.SongAdapter
import com.example.vxsound.adapter.SongGridAdapter
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.FragmentHomeBinding
import com.example.vxsound.listener.IOnClickSongItemListener
import com.example.vxsound.model.Song
import com.example.vxsound.service.MusicService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Collections

class HomeFragment : Fragment() {
    private var mFragmentHomeBinding: FragmentHomeBinding? = null
    private var mListSong: MutableList<Song>? = null
    private var mListSongBanner: MutableList<Song>? = null
    private val mHandlerBanner = Handler(Looper.getMainLooper())
    private val mRunnableBanner = Runnable {
        if (mListSongBanner == null || mListSongBanner!!.isEmpty()) {
            return@Runnable
        }
        if (mFragmentHomeBinding?.viewpager2?.currentItem == mListSongBanner!!.size - 1) {
            mFragmentHomeBinding?.viewpager2?.currentItem = 0
            return@Runnable
        }
        mFragmentHomeBinding?.viewpager2?.currentItem = mFragmentHomeBinding?.viewpager2?.currentItem!! + 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        loadListSongFromFirebase()
        initListener()
        return mFragmentHomeBinding?.root
    }

    private fun initListener() {
        mFragmentHomeBinding?.layoutViewAllFeatured?.setOnClickListener { GlobalFunction.startActivity(activity, FeaturedSongsActivity::class.java) }
        mFragmentHomeBinding?.layoutViewAllPopular?.setOnClickListener { GlobalFunction.startActivity(activity, PopularSongsActivity::class.java) }
        mFragmentHomeBinding?.layoutViewAllNewSongs?.setOnClickListener { GlobalFunction.startActivity(activity, NewSongsActivity::class.java) }
    }

    private fun loadListSongFromFirebase() {
        if (activity == null) return
        MyApplication[activity].songsDatabaseReference()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mFragmentHomeBinding?.layoutContent?.visibility = View.VISIBLE
                resetListSong()
                for (dataSnapshot in snapshot.children) {
                    val song = dataSnapshot.getValue(Song::class.java) ?: return
                    mListSong!!.add(0, song)
                }
                displayListBannerSongs()
                displayListFeaturedSongs()
                displayListNewSongs()
                displayListPopularSongs()
            }

            override fun onCancelled(error: DatabaseError) {
                GlobalFunction.showToastMessage(activity, getString(R.string.msg_get_date_error))
            }
        })
    }

    private fun resetListSong() {
        if (mListSong == null) {
            mListSong = ArrayList()
        } else {
            mListSong!!.clear()
        }
    }

    private fun displayListBannerSongs() {
        val bannerSongAdapter = BannerSongAdapter(listBannerSongs(), object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }

            override fun onClickFavoriteSong(song: Song, favorite: Boolean) {}
        })
        mFragmentHomeBinding?.viewpager2?.adapter = bannerSongAdapter
        mFragmentHomeBinding?.indicator3?.setViewPager(mFragmentHomeBinding?.viewpager2)
        mFragmentHomeBinding?.viewpager2?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mHandlerBanner.removeCallbacks(mRunnableBanner)
                mHandlerBanner.postDelayed(mRunnableBanner, 3000)
            }
        })
    }

    private fun listBannerSongs(): List<Song> {
        if (mListSongBanner != null) {
            mListSongBanner!!.clear()
        } else {
            mListSongBanner = ArrayList()
        }
        if (mListSong == null || mListSong!!.isEmpty()) {
            return mListSongBanner!!
        }
        for (song in mListSong!!) {
            if (song.isFeatured == true && mListSongBanner!!.size < Constant.MAX_COUNT_BANNER) {
                mListSongBanner!!.add(song)
            }
        }
        return mListSongBanner!!
    }

    private fun displayListFeaturedSongs() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mFragmentHomeBinding?.rcvFeaturedSongs?.layoutManager = linearLayoutManager
        val songGridAdapter = SongGridAdapter(listFeaturedSongs(), object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }

            override fun onClickFavoriteSong(song: Song, favorite: Boolean) {
                GlobalFunction.onClickFavoriteSong(activity, song, favorite)
            }
        })
        mFragmentHomeBinding?.rcvFeaturedSongs?.adapter = songGridAdapter
    }

    private fun listFeaturedSongs(): List<Song> {
        val list: MutableList<Song> = ArrayList()
        if (mListSong == null || mListSong!!.isEmpty()) {
            return list
        }
        for (song in mListSong!!) {
            if (song.isFeatured == true && list.size < Constant.MAX_COUNT_FEATURED) {
                list.add(song)
            }
        }
        return list
    }

    private fun displayListNewSongs() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mFragmentHomeBinding?.rcvNewSongs?.layoutManager = linearLayoutManager
        val songGridAdapter = SongGridAdapter(listNewSongs(), object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }

            override fun onClickFavoriteSong(song: Song, favorite: Boolean) {
                GlobalFunction.onClickFavoriteSong(activity, song, favorite)
            }
        })
        mFragmentHomeBinding?.rcvNewSongs?.adapter = songGridAdapter
    }

    private fun listNewSongs(): List<Song> {
        val list: MutableList<Song> = ArrayList()
        if (mListSong == null || mListSong!!.isEmpty()) {
            return list
        }
        for (song in mListSong!!) {
            if (song.isLatest == true && list.size < Constant.MAX_COUNT_LATEST) {
                list.add(song)
            }
        }
        return list
    }

    private fun displayListPopularSongs() {
        if (activity == null) return
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentHomeBinding?.rcvPopularSongs?.layoutManager = linearLayoutManager
        val songAdapter = SongAdapter(listPopularSongs(), object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }

            override fun onClickFavoriteSong(song: Song, favorite: Boolean) {
                GlobalFunction.onClickFavoriteSong(activity, song, favorite)
            }
        })
        mFragmentHomeBinding?.rcvPopularSongs?.adapter = songAdapter
    }

    private fun listPopularSongs(): List<Song> {
        val list: MutableList<Song> = ArrayList()
        if (mListSong == null || mListSong!!.isEmpty()) {
            return list
        }
        val allSongs: List<Song> = ArrayList(mListSong!!)
        Collections.sort(allSongs) { song1: Song, song2: Song -> song2.count - song1.count }
        for (song in allSongs) {
            if (list.size < Constant.MAX_COUNT_POPULAR) {
                list.add(song)
            }
        }
        return list
    }

    private fun goToSongDetail(song: Song) {
        MusicService.clearListSongPlaying()
        MusicService.mListSongPlaying!!.add(song)
        MusicService.isPlaying = false
        GlobalFunction.startMusicService(activity, Constant.PLAY, 0)
        GlobalFunction.startActivity(activity, PlayMusicActivity::class.java)
    }
}
