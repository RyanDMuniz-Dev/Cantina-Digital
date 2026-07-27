package com.example.cantinadigital.data.model

data class Log(
    val id: String = "",
    val userName: String = "",
    val userClass: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
