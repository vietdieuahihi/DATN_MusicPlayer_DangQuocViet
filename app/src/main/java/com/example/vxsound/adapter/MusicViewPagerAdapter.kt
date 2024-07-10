package com.example.vxsound.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vxsound.fragment.ListSongPlayingFragment
import com.example.vxsound.fragment.PlaySongFragment

class MusicViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            ListSongPlayingFragment()
        } else PlaySongFragment()
    }

    override fun getItemCount(): Int {
        return 2
    }
}