package ru.diitcenter.appnote.data.network

import ru.diitcenter.appnote.domain.model.Media
import retrofit2.Call
import retrofit2.http.GET

interface ApiAppNote {

    @GET("/test.json")
    fun getMedia(): Call<Media>
}
