package com.example.vxsound.model

import java.io.Serializable

class Song : Serializable {
    var id: Long = 0
    var title: String? = null
    var image: String? = null
    var url: String? = null
    var artist: String? = null
    var isLatest: Boolean? = false
    var isFeatured: Boolean? = false
    var count = 0
    var isPlaying = false
    var favorite: HashMap<String, UserInfor>? = null

    constructor()

    constructor(id: Long, title: String?, artist: String?, image: String?, url: String?, latest: Boolean?, featured: Boolean?) {
        this.id = id
        this.title = title
        this.artist = artist
        this.image = image
        this.url = url
        isLatest = latest
        isFeatured = featured
    }
}