package com.example.cantinadigital.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Order(

    @DocumentId
    var id: String = "",

    @get:PropertyName("data_hora") @set:PropertyName("data_hora")
    var dateTime: Timestamp? = null,

    @get:PropertyName("funcionario_nome") @set:PropertyName("funcionario_nome")
    var employeeName: String = "",

    @get:PropertyName("funcionario_turma") @set:PropertyName("funcionario_turma")
    var employeeClass: String = "",

    @get:PropertyName("") @set:PropertyName("")
    var payment: String = "",

    @get:PropertyName("itens") @set:PropertyName("itens")
    var items: List<OrderItem> = emptyList(),

    @get:PropertyName("valor_total") @set:PropertyName("valor_total")
    var totalValue: Double = 0.0

)

fun Timestamp.toFormattedString(): String {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("pt", "BR"))
    return sdf.format(this.toDate())
}