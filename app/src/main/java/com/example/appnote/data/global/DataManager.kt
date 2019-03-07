package com.example.appnote.data.global

import com.example.appnote.data.network.ApiAppNote

class DataManager(private val apiAppNote: ApiAppNote) {

    fun getAllMedia() = apiAppNote.getMedia()
}
