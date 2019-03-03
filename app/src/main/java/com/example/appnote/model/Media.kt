package com.example.appnote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Media(
    @SerializedName("type") val type: Int,
    @SerializedName("url") val url: String,
    var name: String = ""
) : Parcelable
