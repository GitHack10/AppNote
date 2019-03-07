package com.example.appnote.data.network

import com.example.appnote.domain.model.Media
import retrofit2.Call
import retrofit2.http.GET

interface ApiAppNote {

    @GET("/test.json")
    fun getMedia(): Call<Media>
}
