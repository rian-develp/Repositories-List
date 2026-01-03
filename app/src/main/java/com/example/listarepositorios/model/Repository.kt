package com.example.listarepositorios.model

import com.google.gson.annotations.SerializedName

data class Repository(
    val name: String,
    @SerializedName("html_url")
    val html_url: String
)
