package com.example.cantinadigital.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class Payout(

    @DocumentId
    val id: String = "",

    @get:PropertyName("vendedor")
    @set:PropertyName("vendedor")
    var vendedor: String = "",

    @get:PropertyName("valor_bruto")
    @set:PropertyName("valor_bruto")
    var valorBruto: Double = 0.0,

    @get:PropertyName("taxa_cantina")
    @set:PropertyName("taxa_cantina")
    var taxaCantina: Double = 0.0,

    @get:PropertyName("valor_repassado")
    @set:PropertyName("valor_repassado")
    var valorRepassado: Double = 0.0,

    @get:PropertyName("produtos")
    @set:PropertyName("produtos")
    var produtos: List<PayoutProduct> = emptyList(),

    @get:PropertyName("funcionario_id")
    @set:PropertyName("funcionario_id")
    var funcionarioId: String = "",

    @get:PropertyName("funcionario_nome")
    @set:PropertyName("funcionario_nome")
    var funcionarioNome: String = "",

    @get:PropertyName("data_hora")
    @set:PropertyName("data_hora")
    @ServerTimestamp
    var dataHora: Timestamp? = null

)
