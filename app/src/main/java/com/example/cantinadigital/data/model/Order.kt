package com.example.cantinadigital.data.model

data class Order(
    val id: String = "",
    val userName: String = "",
    val userClass: String = "",
    val totalPrice: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
