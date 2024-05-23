package com.example.vxsound.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vxsound.R
import com.example.vxsound.adapter.SongAdapter.SongViewHolder
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.ItemSongBinding
import com.example.vxsound.listener.IOnClickSongItemListener
import com.example.vxsound.model.Song
import com.example.vxsound.utils.GlideUtils
import kotlinx.android.synthetic.main.item_admin_song.view.tv_name
import kotlinx.android.synthetic.main.item_song.view.tv_artist
import kotlinx.android.synthetic.main.item_song.view.tv_song_name

class SongAdapter(
    private val mListSongs: List<Song>?,
    private val iOnClickSongItemListener: IOnClickSongItemListener
) : RecyclerView.Adapter<SongViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(itemSongBinding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = mListSongs!![position]
        GlideUtils.loadUrl(song.image, holder.mItemSongBinding.imgSong)
        holder.mItemSongBinding.tvSongName.text = song.title
        holder.mItemSongBinding.tvArtist.text = song.artist

        val context = holder.itemView.context
        val strListen: String = if (song.count > 1) {
            context.getString(R.string.listens)
        } else {
            context.getString(R.string.listen)
        }

        val strCountListen = song.count.toString() + " " + strListen
        holder.mItemSongBinding.tvCountListen.text = strCountListen

        val isFavorite = GlobalFunction.isFavoriteSong(song)
        if (isFavorite) {
            holder.mItemSongBinding.imgFavorite.setImageResource(R.drawable.ic_favorite)
        } else {
            holder.mItemSongBinding.imgFavorite.setImageResource(R.drawable.ic_unfavorite)
        }

        holder.mItemSongBinding.layoutItem.setOnClickListener { iOnClickSongItemListener.onClickItemSong(song) }
        holder.mItemSongBinding.imgFavorite.setOnClickListener { iOnClickSongItemListener.onClickFavoriteSong(song, !isFavorite) }

        //Chay chu
        holder.itemView.tv_song_name.isSelected = true
        holder.itemView.tv_song_name.requestFocus()

        holder.itemView.tv_artist.isSelected = true
        holder.itemView.tv_artist.requestFocus()
    }

    override fun getItemCount(): Int {
        return mListSongs?.size ?: 0
    }

    class SongViewHolder(val mItemSongBinding: ItemSongBinding) : RecyclerView.ViewHolder(mItemSongBinding.root)
}
