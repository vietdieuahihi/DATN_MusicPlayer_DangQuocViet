package com.example.vxsound.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.vxsound.R
import com.example.vxsound.adapter.UserMainViewPagerAdapter
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.ActivityMainBinding
import com.example.vxsound.model.Song
import com.example.vxsound.service.MusicService
import com.example.vxsound.utils.GlideUtils
import kotlinx.android.synthetic.main.item_song.view.tv_artist
import java.util.Locale

@SuppressLint("NonConstantResourceId")
class MainActivity : BaseActivity(), View.OnClickListener {

    private var mActivityMainBinding: ActivityMainBinding? = null
    private var mAction = 0
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0)
            handleMusicAction()
        }
    }

    private val languageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val lang = intent.getStringExtra("lang") ?: return
            updateLocale(lang)
        }
    }

    private val darkModeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isDarkMode = intent.getBooleanExtra("isDarkMode", false)
            setDarkMode(isDarkMode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Áp dụng ngôn ngữ lưu trữ trước khi thiết lập giao diện
        val sharedPreferences = getSharedPreferences("LangPrefs", Context.MODE_PRIVATE)
        val lang = sharedPreferences.getString("app_language", "en")
        setLocale(lang!!)

        mActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mActivityMainBinding?.root)
        checkNotificationPermission()
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
            IntentFilter(Constant.CHANGE_LISTENER))
        LocalBroadcastManager.getInstance(this).registerReceiver(languageReceiver,
            IntentFilter(Constant.CHANGE_LANGUAGE))
        LocalBroadcastManager.getInstance(this).registerReceiver(darkModeReceiver,
            IntentFilter(Constant.CHANGE_DARK_MODE))
        setupActivity()
        initListener()
        displayLayoutBottom()

        //
        val isDarkMode = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            .getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        //chay chu

        mActivityMainBinding!!.layoutBottom.tvSongName.isSelected = true
        mActivityMainBinding!!.layoutBottom.tvSongName.requestFocus()

        mActivityMainBinding!!.layoutBottom.tvArtist.isSelected = true
        mActivityMainBinding!!.layoutBottom.tvArtist.requestFocus()
//        holder.itemView.tv_artist.isSelected = true
//        holder.itemView.tv_artist.requestFocus()
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun updateLocale(lang: String) {
        setLocale(lang)
        recreate()
    }

    private fun setDarkMode(isDarkMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
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
        mActivityMainBinding?.viewpager2?.isUserInputEnabled = false
        val userMainViewPagerAdapter = UserMainViewPagerAdapter(this)
        mActivityMainBinding?.viewpager2?.adapter = userMainViewPagerAdapter
        mActivityMainBinding?.viewpager2?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        mActivityMainBinding?.bottomNavigation?.menu?.findItem(R.id.nav_home)?.isChecked = true
                        mActivityMainBinding?.header?.tvTitle?.text = getString(R.string.menu_home)
                    }

                    1 -> {
                        mActivityMainBinding?.bottomNavigation?.menu?.findItem(R.id.nav_search)?.isChecked = true
                        mActivityMainBinding?.header?.tvTitle?.text = getString(R.string.menu_search)
                    }

                    2 -> {
                        mActivityMainBinding?.bottomNavigation?.menu?.findItem(R.id.nav_favorite)?.isChecked = true
                        mActivityMainBinding?.header?.tvTitle?.text = getString(R.string.menu_favorite_songs)
                    }

                    3 -> {
                        mActivityMainBinding?.bottomNavigation?.menu?.findItem(R.id.nav_settings)?.isChecked = true
                        mActivityMainBinding?.header?.tvTitle?.text = getString(R.string.menu_settings)
                    }
                }
            }
        })
        mActivityMainBinding?.bottomNavigation?.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home -> {
                    mActivityMainBinding?.viewpager2?.currentItem = 0
                    mActivityMainBinding?.header?.tvTitle?.text = getString(R.string.menu_home)
                }
                R.id.nav_search -> {
                    mActivityMainBinding?.viewpager2?.currentItem = 1
                    mActivityMainBinding?.header?.tvTitle?.text = getString(R.string.menu_search)
                }
                R.id.nav_favorite -> {
                    mActivityMainBinding?.viewpager2?.currentItem = 2
                    mActivityMainBinding?.header?.tvTitle?.text = getString(R.string.menu_favorite_songs)
                }
                R.id.nav_settings -> {
                    mActivityMainBinding?.viewpager2?.currentItem = 3
                    mActivityMainBinding?.header?.tvTitle?.text = getString(R.string.menu_settings)
                }
            }
            true
        }
    }

    private fun initListener() {
        mActivityMainBinding?.layoutBottom?.imgPrevious?.setOnClickListener(this)
        mActivityMainBinding?.layoutBottom?.imgPlay?.setOnClickListener(this)
        mActivityMainBinding?.layoutBottom?.imgNext?.setOnClickListener(this)
        mActivityMainBinding?.layoutBottom?.imgClose?.setOnClickListener(this)
        mActivityMainBinding?.layoutBottom?.layoutText?.setOnClickListener(this)
        mActivityMainBinding?.layoutBottom?.imgSong?.setOnClickListener(this)
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
            mActivityMainBinding?.layoutBottom?.layoutItem?.visibility = View.GONE
            return
        }
        mActivityMainBinding?.layoutBottom?.layoutItem?.visibility = View.VISIBLE
        showInfoSong()
        showStatusButtonPlay()
    }

    private fun handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            mActivityMainBinding?.layoutBottom?.layoutItem?.visibility = View.GONE
            return
        }
        mActivityMainBinding?.layoutBottom?.layoutItem?.visibility = View.VISIBLE
        showInfoSong()
        showStatusButtonPlay()
    }

    private fun showInfoSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying!!.isEmpty()) {
            return
        }
        val currentSong: Song = MusicService.mListSongPlaying!![MusicService.mSongPosition]
        mActivityMainBinding?.layoutBottom?.tvSongName?.text = currentSong.title
        mActivityMainBinding?.layoutBottom?.tvArtist?.text = currentSong.artist
        GlideUtils.loadUrl(currentSong.image, mActivityMainBinding?.layoutBottom?.imgSong!!)
    }

    private fun showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            mActivityMainBinding?.layoutBottom?.imgPlay?.setImageResource(R.drawable.ic_pause_black)
        } else {
            mActivityMainBinding?.layoutBottom?.imgPlay?.setImageResource(R.drawable.ic_play_black)
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

    override fun onDestroy() {
        // Hủy đăng ký languageReceiver và mBroadcastReceiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(languageReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(darkModeReceiver)
        super.onDestroy()
    }

    override fun onBackPressed() {
        showConfirmExitApp()
    }
}
