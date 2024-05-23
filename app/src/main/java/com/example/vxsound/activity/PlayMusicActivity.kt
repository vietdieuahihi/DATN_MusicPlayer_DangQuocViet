package com.example.vxsound.activity

import android.os.Bundle
import android.view.View
import com.example.vxsound.R
import com.example.vxsound.adapter.MusicViewPagerAdapter
import com.example.vxsound.databinding.ActivityPlayMusicBinding

class PlayMusicActivity : BaseActivity() {

    private var mActivityPlayMusicBinding: ActivityPlayMusicBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityPlayMusicBinding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(mActivityPlayMusicBinding?.root)
        initToolbar()
        initUI()
    }

    private fun initToolbar() {
        mActivityPlayMusicBinding?.toolbar?.imgBack?.visibility = View.VISIBLE
        mActivityPlayMusicBinding?.toolbar?.imgBack?.setImageResource(R.drawable.ic_back_white)
        mActivityPlayMusicBinding?.toolbar?.tvTitle?.setText(R.string.music_player)
        mActivityPlayMusicBinding?.toolbar?.imgBack?.setOnClickListener { onBackPressed() }
    }

    private fun initUI() {
        val musicViewPagerAdapter = MusicViewPagerAdapter(this)
        mActivityPlayMusicBinding?.viewpager2?.adapter = musicViewPagerAdapter
        mActivityPlayMusicBinding?.indicator3?.setViewPager(mActivityPlayMusicBinding?.viewpager2)
        mActivityPlayMusicBinding?.viewpager2?.currentItem = 1
    }
}