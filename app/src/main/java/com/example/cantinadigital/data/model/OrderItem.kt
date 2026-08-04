package com.example.cantinadigital.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class OrderItem(

    @get:PropertyName("nome") @set:PropertyName("nome")
    var name: String = "",

    var amount: Int = 0

)
