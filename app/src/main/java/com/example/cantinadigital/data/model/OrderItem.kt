package com.example.cantinadigital.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class OrderItem(

    @get:PropertyName("produto_id") @set:PropertyName("produto_id")
    var productId: String = "",

    @get:PropertyName("nome") @set:PropertyName("nome")
    var name: String = "",

    @get:PropertyName("quantidade") @set:PropertyName("quantidade")
    var amount: Int = 0,

    @get:PropertyName("valor_unidade") @set:PropertyName("valor_unidade")
    var unitValue: Double = 0.0,

    @get:PropertyName("vendedor") @set:PropertyName("vendedor")
    var vendedor: String = "Cantina",

    @get:PropertyName("sala")
    @set:PropertyName("sala")
    var sala: String = "",

    @get:PropertyName("taxa_cantina") @set:PropertyName("taxa_cantina")
    var taxaCantina: Double = 0.0

)
