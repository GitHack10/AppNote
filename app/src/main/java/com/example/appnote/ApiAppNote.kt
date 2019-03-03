package com.example.appnote

import com.example.appnote.model.Media
import retrofit2.Call
import retrofit2.http.GET

interface ApiAppNote {

    @GET("/test.json")
    fun getMedia(): Call<List<Media>>
}
