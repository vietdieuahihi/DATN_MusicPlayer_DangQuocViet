package com.example.vxsound.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vxsound.R
import com.example.vxsound.adapter.SongGridAdapter.SongGridViewHolder
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.ItemSongGridBinding
import com.example.vxsound.listener.IOnClickSongItemListener
import com.example.vxsound.model.Song
import com.example.vxsound.utils.GlideUtils
import kotlinx.android.synthetic.main.item_song.view.tv_artist
import kotlinx.android.synthetic.main.item_song_grid.view.tv_artist
import kotlinx.android.synthetic.main.item_song_grid.view.tv_song_name

class SongGridAdapter(private val mListSongs: List<Song>?, private val iOnClickSongItemListener: IOnClickSongItemListener) : RecyclerView.Adapter<SongGridViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongGridViewHolder {
        val itemSongGridBinding = ItemSongGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongGridViewHolder(itemSongGridBinding)
    }

    override fun onBindViewHolder(holder: SongGridViewHolder, position: Int) {
        val song = mListSongs!![position]
        GlideUtils.loadUrl(song.image, holder.mItemSongGridBinding.imgSong)
        holder.mItemSongGridBinding.tvSongName.text = song.title
        holder.mItemSongGridBinding.tvArtist.text = song.artist
        val context = holder.itemView.context
        val strListen = if (song.count > 1) {
            context.getString(R.string.listens)
        } else {
            context.getString(R.string.listen)
        }
        val strCountListen = song.count.toString() + " " + strListen
        holder.mItemSongGridBinding.tvCountListen.text = strCountListen
        val isFavorite = GlobalFunction.isFavoriteSong(song)
        if (isFavorite) {
            holder.mItemSongGridBinding.imgFavorite.setImageResource(R.drawable.ic_favorite)
        } else {
            holder.mItemSongGridBinding.imgFavorite.setImageResource(R.drawable.ic_unfavorite)
        }
        holder.mItemSongGridBinding.layoutItem.setOnClickListener { iOnClickSongItemListener.onClickItemSong(song) }
        holder.mItemSongGridBinding.imgFavorite.setOnClickListener { iOnClickSongItemListener.onClickFavoriteSong(song, !isFavorite) }

        //chay chu
        holder.itemView.tv_song_name.isSelected = true
        holder.itemView.tv_song_name.requestFocus()
    }


    override fun getItemCount(): Int {
        return mListSongs?.size ?: 0
    }

    class SongGridViewHolder(val mItemSongGridBinding: ItemSongGridBinding) : RecyclerView.ViewHolder(mItemSongGridBinding.root)
}