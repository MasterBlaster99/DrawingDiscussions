package com.example.drawingdiscussions.model

import com.google.gson.annotations.SerializedName

data class Drawing(
    val title: String?="",
    val imageUrl: String?="",
    val timeStamp: String?="",
    @SerializedName("markersList") val markersList: ArrayList<HashMap<String, String>>?=ArrayList()
)
