package com.example.vxsound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vxsound.R
import com.example.vxsound.adapter.AdminSongAdapter.AdminSongViewHolder
import com.example.vxsound.databinding.ItemAdminSongBinding
import com.example.vxsound.listener.IOnManagerSongListener
import com.example.vxsound.model.Song
import com.example.vxsound.utils.GlideUtils
import kotlinx.android.synthetic.main.item_admin_song.view.tv_artist
import kotlinx.android.synthetic.main.item_admin_song.view.tv_name

class AdminSongAdapter(private val mListSongs: List<Song>?, private val iOnManagerSongListener: IOnManagerSongListener) : RecyclerView.Adapter<AdminSongViewHolder?>() {

//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val adminSong: TextView = itemView.findViewById(R.id.tv_name)
//    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminSongViewHolder {
        val itemAdminSongBinding = ItemAdminSongBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminSongViewHolder(itemAdminSongBinding)
    }

    override fun onBindViewHolder(holder: AdminSongViewHolder, position: Int) {
        val song = mListSongs!![position]
        GlideUtils.loadUrl(song.image, holder.mItemAdminSongBinding.imgSong)
        holder.mItemAdminSongBinding.tvName.text = song.title
        holder.mItemAdminSongBinding.tvArtist.text = song.artist
        if (song.isFeatured == true) {
            holder.mItemAdminSongBinding.tvFeatured.text = "Yes"
        } else {
            holder.mItemAdminSongBinding.tvFeatured.text = "No"
        }
        if (song.isLatest == true) {
            holder.mItemAdminSongBinding.tvLatest.text = "Yes"
        } else {
            holder.mItemAdminSongBinding.tvLatest.text = "No"
        }
        holder.mItemAdminSongBinding.layoutImage.setOnClickListener { iOnManagerSongListener.onClickItemSong(song) }
        holder.mItemAdminSongBinding.layoutInfor.setOnClickListener { iOnManagerSongListener.onClickItemSong(song) }
        holder.mItemAdminSongBinding.imgEdit.setOnClickListener { iOnManagerSongListener.onClickUpdateSong(song) }
        holder.mItemAdminSongBinding.imgDelete.setOnClickListener { iOnManagerSongListener.onClickDeleteSong(song) }

        //
        holder.itemView.tv_name.isSelected = true
        holder.itemView.tv_name.requestFocus()

        holder.itemView.tv_artist.isSelected = true
        holder.itemView.tv_artist.requestFocus()
    }

    override fun getItemCount(): Int {
        return mListSongs?.size ?: 0
    }

    class AdminSongViewHolder(val mItemAdminSongBinding: ItemAdminSongBinding) : RecyclerView.ViewHolder(mItemAdminSongBinding.root)
}