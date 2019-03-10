package com.example.appnote.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Media(
    @SerializedName("text") val text: String,
    @SerializedName("data") var dataList: List<Data>
) : Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("type") val type: Int,
        @SerializedName("url") var url: String,
        var name: String = ""
    ): Parcelable
}
