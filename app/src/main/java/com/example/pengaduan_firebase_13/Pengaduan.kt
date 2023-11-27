package com.example.pengaduan_firebase_13

import java.io.Serializable

data class Pengaduan(
    var id: String = "",
    var name: String = "",
    var title: String = "",
    var aduan: String = ""
) : Serializable
