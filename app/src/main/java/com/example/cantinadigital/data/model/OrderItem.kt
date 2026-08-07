package com.example.cantinadigital.data.model

import com.google.firebase.firestore.PropertyName

data class OrderItem(

    @get:PropertyName("nome") @set:PropertyName("nome")
    var name: String = "",

    @get:PropertyName("quantidade") @set:PropertyName("quantidade")
    var amount: Int = 0,

    @get:PropertyName("valor_unidade") @set:PropertyName("valor_unidade")
    var unitValue: Double = 0.0,

    @get:PropertyName("vendedor") @set:PropertyName("vendedor")
    var vendedor: String = "Cantina",

    @get:PropertyName("taxa_cantina") @set:PropertyName("taxa_cantina")
    var taxaCantinaPorcentagem: Double = 0.0

)
