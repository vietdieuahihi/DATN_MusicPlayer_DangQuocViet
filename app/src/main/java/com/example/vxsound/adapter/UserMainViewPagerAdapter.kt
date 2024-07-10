package com.example.vxsound.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vxsound.fragment.FavoriteFragment
import com.example.vxsound.fragment.HomeFragment
import com.example.vxsound.fragment.SearchFragment
import com.example.vxsound.fragment.SettingsFragment

class UserMainViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> SearchFragment()
            2 -> FavoriteFragment()
            3 -> SettingsFragment()
            else -> HomeFragment()
        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}