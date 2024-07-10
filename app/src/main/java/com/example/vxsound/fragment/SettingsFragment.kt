package com.example.vxsound.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.vxsound.activity.ChangePasswordActivity
import com.example.vxsound.activity.ContactActivity
import com.example.vxsound.activity.FeedbackActivity
import com.example.vxsound.activity.SignInActivity
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.FragmentSettingsBinding
import com.example.vxsound.prefs.DataStoreManager
import com.example.vxsound.service.MusicService
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class SettingsFragment : Fragment() {
    private var mFragmentSettingsBinding: FragmentSettingsBinding? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireActivity().getSharedPreferences("LangPrefs", Context.MODE_PRIVATE)
        initUi()
        initListener()
        return mFragmentSettingsBinding?.root
    }

    private fun initUi() {
        mFragmentSettingsBinding?.tvEmail?.text = DataStoreManager.user?.email
        if (DataStoreManager.user?.isAdmin == true) {
            mFragmentSettingsBinding?.tvFeedback?.visibility = View.GONE
            mFragmentSettingsBinding?.tvContact?.visibility = View.GONE
        } else {
            mFragmentSettingsBinding?.tvFeedback?.visibility = View.VISIBLE
            mFragmentSettingsBinding?.tvContact?.visibility = View.VISIBLE
        }
    }

    private fun initListener() {
        mFragmentSettingsBinding?.tvFeedback?.setOnClickListener {
            GlobalFunction.startActivity(activity, FeedbackActivity::class.java)
        }
        mFragmentSettingsBinding?.tvContact?.setOnClickListener {
            GlobalFunction.startActivity(activity, ContactActivity::class.java)
        }
        mFragmentSettingsBinding?.tvChangePassword?.setOnClickListener {
            GlobalFunction.startActivity(activity, ChangePasswordActivity::class.java)
        }
        mFragmentSettingsBinding?.tvSignOut?.setOnClickListener {
            onClickSignOut()
        }
        mFragmentSettingsBinding?.langVietnamese?.setOnClickListener {
            setLocale("vi")
        }
        mFragmentSettingsBinding?.langEnglish?.setOnClickListener {
            setLocale("en")
        }

        mFragmentSettingsBinding?.modeSwitch?.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        mFragmentSettingsBinding?.modeSwitch?.setOnCheckedChangeListener { _, isChecked ->
            setDarkMode(isChecked)
        }
    }

    private fun setDarkMode(isDarkMode: Boolean) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("dark_mode", isDarkMode).apply()

        // Send broadcast to MainActivity to update dark mode
        val intent = Intent(Constant.CHANGE_DARK_MODE)
        intent.putExtra("isDarkMode", isDarkMode)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun onClickSignOut() {
        if (activity == null) return
        FirebaseAuth.getInstance().signOut()
        DataStoreManager.user = null
        // Stop service when user sign out
        GlobalFunction.startMusicService(
            activity,
            Constant.CANNEL_NOTIFICATION,
            MusicService.mSongPosition
        )
        GlobalFunction.startActivity(activity, SignInActivity::class.java)
        activity!!.finishAffinity()
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        val sharedPreferences =
            requireActivity().getSharedPreferences("LangPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("app_language", lang).apply()

        // Send broadcast to MainActivity to update locale
        val intent = Intent(Constant.CHANGE_LANGUAGE)
        intent.putExtra("lang", lang)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }
}
