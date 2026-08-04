package com.example.cantinadigital.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Order(

    @DocumentId
    val id: String = "",

    @get:PropertyName("")
    val userName: String = "",

    val userClass: String = "",

    val totalPrice: Double = 0.0,

    val timestamp: Long = System.currentTimeMillis()
)
