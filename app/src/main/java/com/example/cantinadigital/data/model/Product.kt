package com.example.cantinadigital.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Product(
    @DocumentId
    val id: String = "",

    @get:PropertyName("emoji") @set:PropertyName("emoji")
    var emoji: String = "",

    @get:PropertyName("nome") @set:PropertyName("nome")
    var nome: String = "",

    @get:PropertyName("quantidade") @set:PropertyName("quantidade")
    var quantidade: Int = 0,

    @get:PropertyName("valor") @set:PropertyName("valor")
    var valor: Double = 0.0,

    @get:PropertyName("vendedor") @set:PropertyName("vendedor")
    var vendedor: String = "Cantina",

    @get:PropertyName("sala") @set:PropertyName("sala")
    var sala: String = "",

    @get:PropertyName("taxa_cantina") @set:PropertyName("taxa_cantina")
    var cantinaTaxa: Double = 0.0,
)