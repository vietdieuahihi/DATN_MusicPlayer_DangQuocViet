package com.example.vxsound.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vxsound.fragment.AdminFeedbackFragment
import com.example.vxsound.fragment.AdminHomeFragment
import com.example.vxsound.fragment.SettingsFragment

class AdminMainViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> AdminFeedbackFragment()
            2 -> SettingsFragment()
            else -> AdminHomeFragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}