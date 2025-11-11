package com.darkzide.tvchannels.model

data class Channel(
    val id: String,
    val name: String,
    val logo: String,
    val url: String,
    val group: String = ""
)