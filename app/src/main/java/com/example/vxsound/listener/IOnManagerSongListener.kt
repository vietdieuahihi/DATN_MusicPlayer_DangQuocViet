package com.example.vxsound.listener

import com.example.vxsound.model.Song

interface IOnManagerSongListener {
    fun onClickItemSong(song: Song)
    fun onClickUpdateSong(song: Song)
    fun onClickDeleteSong(song: Song)
}