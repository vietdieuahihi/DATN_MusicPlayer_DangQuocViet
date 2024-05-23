package com.example.vxsound.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vxsound.adapter.BannerSongAdapter.BannerSongViewHolder
import com.example.vxsound.databinding.ItemBannerSongBinding
import com.example.vxsound.listener.IOnClickSongItemListener
import com.example.vxsound.model.Song
import com.example.vxsound.utils.GlideUtils

class BannerSongAdapter(private val mListSongs: List<Song>?, private val iOnClickSongItemListener: IOnClickSongItemListener) : RecyclerView.Adapter<BannerSongViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerSongViewHolder {
        val itemBannerSongBinding = ItemBannerSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerSongViewHolder(itemBannerSongBinding)
    }

    override fun onBindViewHolder(holder: BannerSongViewHolder, position: Int) {
        val song = mListSongs!![position]
        GlideUtils.loadUrlBanner(song.image, holder.mItemBannerSongBinding.imageBanner)
        holder.mItemBannerSongBinding.layoutItem.setOnClickListener { iOnClickSongItemListener.onClickItemSong(song) }
    }

    override fun getItemCount(): Int {
        return mListSongs?.size ?: 0
    }

    class BannerSongViewHolder(val mItemBannerSongBinding: ItemBannerSongBinding) : RecyclerView.ViewHolder(mItemBannerSongBinding.root)
}