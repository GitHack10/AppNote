package com.example.appnote

class DataManager(private val apiAppNote: ApiAppNote) {

    fun getAllMedia() = apiAppNote.getMedia()
}
