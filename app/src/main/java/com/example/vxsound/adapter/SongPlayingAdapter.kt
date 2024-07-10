package com.example.vxsound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vxsound.R
import com.example.vxsound.adapter.SongPlayingAdapter.SongPlayingViewHolder
import com.example.vxsound.databinding.ItemSongPlayingBinding
import com.example.vxsound.listener.IOnClickSongPlayingItemListener
import com.example.vxsound.model.Song
import com.example.vxsound.utils.GlideUtils
import kotlinx.android.synthetic.main.item_song_grid.view.tv_song_name
import kotlinx.android.synthetic.main.layout_control_bottom.view.tv_song_name

class SongPlayingAdapter(private val mListSongs: List<Song>?, private val iOnClickSongPlayingItemListener: IOnClickSongPlayingItemListener) : RecyclerView.Adapter<SongPlayingViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongPlayingViewHolder {
        val itemSongPlayingBinding = ItemSongPlayingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongPlayingViewHolder(itemSongPlayingBinding)
    }

    override fun onBindViewHolder(holder: SongPlayingViewHolder, position: Int) {
        val song = mListSongs!![position]
        if (song.isPlaying) {
            holder.mItemSongPlayingBinding.layoutItem.setBackgroundResource(R.color.background_bottom)
            holder.mItemSongPlayingBinding.imgPlaying.visibility = View.VISIBLE
        } else {
            holder.mItemSongPlayingBinding.layoutItem.setBackgroundResource(R.color.bgrplaying)
            holder.mItemSongPlayingBinding.imgPlaying.visibility = View.GONE
        }
        GlideUtils.loadUrl(song.image, holder.mItemSongPlayingBinding.imgSong)
        holder.mItemSongPlayingBinding.tvSongName.text = song.title
        holder.mItemSongPlayingBinding.tvArtist.text = song.artist
        holder.mItemSongPlayingBinding.layoutItem.setOnClickListener { iOnClickSongPlayingItemListener.onClickItemSongPlaying(holder.adapterPosition) }

    }

    override fun getItemCount(): Int {
        return mListSongs?.size ?: 0
    }

    class SongPlayingViewHolder(val mItemSongPlayingBinding: ItemSongPlayingBinding) : RecyclerView.ViewHolder(mItemSongPlayingBinding.root)
}