package com.example.cantinadigital.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.DocumentId

data class FinancialTransaction (

    @DocumentId
    val id: String = "",

    @get:PropertyName("tipo")
    @set:PropertyName("tipo")
    var tipo: String = "SAIDA", // "ENTRADA" ou "SAIDA"

    @get:PropertyName("valor")
    @set:PropertyName("valor")
    var valor: Double = 0.0,

    @get:PropertyName("motivo")
    @set:PropertyName("motivo")
    var motivo: String = "", // Ex: "Compra de copos descartáveis", "Saco de pão"

    @get:PropertyName("funcionario_nome")
    @set:PropertyName("funcionario_nome")
    var funcionarioNome: String = "",

    @get:PropertyName("data_hora")
    @set:PropertyName("data_hora")
    var dataHora: Timestamp? = null

)