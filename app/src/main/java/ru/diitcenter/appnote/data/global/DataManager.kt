package ru.diitcenter.appnote.data.global

import ru.diitcenter.appnote.data.network.ApiAppNote

class DataManager(private val apiAppNote: ApiAppNote) {

    fun getAllMedia() = apiAppNote.getMedia()
}
