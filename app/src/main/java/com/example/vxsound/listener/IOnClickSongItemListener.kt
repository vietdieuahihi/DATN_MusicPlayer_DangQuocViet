package com.example.vxsound.listener

import com.example.vxsound.model.Song

interface IOnClickSongItemListener {
    fun onClickItemSong(song: Song)
    fun onClickFavoriteSong(song: Song, favorite: Boolean)
}