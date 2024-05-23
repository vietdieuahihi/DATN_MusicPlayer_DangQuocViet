package com.example.vxsound.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.vxsound.R
import com.example.vxsound.adapter.AdminMainViewPagerAdapter
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.ActivityAdminMainBinding
import com.example.vxsound.model.Song
import com.example.vxsound.service.MusicService
import com.example.vxsound.utils.GlideUtils

@SuppressLint("NonConstantResourceId")
class AdminMainActivity : BaseActivity(), View.OnClickListener {
    private var mActivityAdminMainBinding: ActivityAdminMainBinding? = null
    private var mAction = 0
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0)
            handleMusicAction()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAdminMainBinding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(mActivityAdminMainBinding?.root)
        checkNotificationPermission()
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                IntentFilter(Constant.CHANGE_LISTENER))
        setupActivity()
        initListener()
        displayLayoutBottom()
        //
//        val tv : TextView = findViewById(R.id.tv_song_name)
//        tv.requestFocus()
//        tv.isSelected = true
//
//        val songAdmin : TextView = findViewById(R.id.tv_name)
//        songAdmin.requestFocus()

    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    private fun setupActivity() {
        mActivityAdminMainBinding?.viewpager2?.isUserInputEnabled = false
        val adminMainViewPagerAdapter = AdminMainViewPagerAdapter(this)
        mActivityAdminMainBinding?.viewpager2?.adapter = adminMainViewPagerAdapter
        mActivityAdminMainBinding?.viewpager2?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        mActivityAdminMainBinding?.bottomNavigation?.menu?.findItem(R.id.nav_home)?.isChecked = true
                        mActivityAdminMainBinding?.header?.tvTitle?.text = getString(R.string.menu_home)
                    }

                    1 -> {
                        mActivityAdminMainBinding?.bottomNavigation?.menu?.findItem(R.id.nav_feedback)?.isChecked = true
                        mActivityAdminMainBinding?.header?.tvTitle?.text = getString(R.string.menu_feedback)
                    }

                    2 -> {
                        mActivityAdminMainBinding?.bottomNavigation?.menu?.findItem(R.id.nav_settings)?.isChecked = true
                        mActivityAdminMainBinding?.header?.tvTitle?.text = getString(R.string.menu_settings)
                    }
                }
            }
        })
        mActivityAdminMainBinding?.bottomNavigation?.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home -> {
                    mActivityAdminMainBinding?.viewpager2?.currentItem = 0
                    mActivityAdminMainBinding?.header?.tvTitle?.text = getString(R.string.menu_home)
                }
                R.id.nav_feedback -> {
                    mActivityAdminMainBinding?.viewpager2?.currentItem = 1
                    mActivityAdminMainBinding?.header?.tvTitle?.text = getString(R.string.menu_feedback)
                }
                R.id.nav_settings -> {
                    mActivityAdminMainBinding?.viewpager2?.currentItem = 2
                    mActivityAdminMainBinding?.header?.tvTitle?.text = getString(R.string.menu_settings)
                }
            }
            true
        }
    }

    private fun initListener() {
        mActivityAdminMainBinding?.layoutBottom?.imgPrevious?.setOnClickListener(this)
        mActivityAdminMainBinding?.layoutBottom?.imgPlay?.setOnClickListener(this)
        mActivityAdminMainBinding?.layoutBottom?.imgNext?.setOnClickListener(this)
        mActivityAdminMainBinding?.layoutBottom?.imgClose?.setOnClickListener(this)
        mActivityAdminMainBinding?.layoutBottom?.layoutText?.setOnClickListener(this)
        mActivityAdminMainBinding?.layoutBottom?.imgSong?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_previous -> clickOnPrevButton()
            R.id.img_play -> clickOnPlayButton()
            R.id.img_next -> clickOnNextButton()
            R.id.img_close -> clickOnCloseButton()
            R.id.layout_text, R.id.img_song -> openPlayMusicActivity()
        }
    }

    private fun showConfirmExitApp() {
        MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive { _: MaterialDialog?, _: DialogAction? -> finish() }
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show()
    }

    private fun displayLayoutBottom() {
        if (MusicService.mPlayer == null) {
            mActivityAdminMainBinding?.layoutBottom?.layoutItem?.visibility = View.GONE
            return
        }
        mActivityAdminMainBinding?.layoutBottom?.layoutItem?.visibility = View.VISIBLE
        showInfoSong()
        showStatusButtonPlay()
    }

    private fun handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            mActivityAdminMainBinding?.layoutBottom?.layoutItem?.visibility = View.GONE
            return
        }
        mActivityAdminMainBinding?.layoutBottom?.layoutItem?.visibility = View.VISIBLE
        showInfoSong()
        showStatusButtonPlay()
    }

    private fun showInfoSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying!!.isEmpty()) {
            return
        }
        val currentSong: Song = MusicService.mListSongPlaying!![MusicService.mSongPosition]
        mActivityAdminMainBinding?.layoutBottom?.tvSongName?.text = currentSong.title
        mActivityAdminMainBinding?.layoutBottom?.tvArtist?.text = currentSong.artist
        GlideUtils.loadUrl(currentSong.image, mActivityAdminMainBinding?.layoutBottom?.imgSong!!)
    }

    private fun showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            mActivityAdminMainBinding?.layoutBottom?.imgPlay?.setImageResource(R.drawable.ic_pause_black)
        } else {
            mActivityAdminMainBinding?.layoutBottom?.imgPlay?.setImageResource(R.drawable.ic_play_black)
        }
    }

    private fun clickOnPrevButton() {
        GlobalFunction.startMusicService(this, Constant.PREVIOUS, MusicService.mSongPosition)
    }

    private fun clickOnNextButton() {
        GlobalFunction.startMusicService(this, Constant.NEXT, MusicService.mSongPosition)
    }

    private fun clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFunction.startMusicService(this, Constant.PAUSE, MusicService.mSongPosition)
        } else {
            GlobalFunction.startMusicService(this, Constant.RESUME, MusicService.mSongPosition)
        }
    }

    private fun clickOnCloseButton() {
        GlobalFunction.startMusicService(this, Constant.CANNEL_NOTIFICATION, MusicService.mSongPosition)
    }

    private fun openPlayMusicActivity() {
        GlobalFunction.startActivity(this, PlayMusicActivity::class.java)
    }

    override fun onBackPressed() {
        showConfirmExitApp()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver)
    }
}