package com.example.cantinadigital.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class AuditLog(

    @DocumentId
    val id: String = "",

    @get:PropertyName("tipo") @set:PropertyName("tipo")
    var type: String = "",

    @get:PropertyName("acao") @set:PropertyName("acao")
    var action: String = "",

    @get:PropertyName("descricao") @set:PropertyName("descricao")
    var description: String = "",

    @get:PropertyName("usuario_nome") @set:PropertyName("usuario_nome")
    var username: String = "",

    @get:PropertyName("usuario_turma") @set:PropertyName("usuario_turma")
    var userClass: String = "",

    @get:PropertyName("data_hora") @set:PropertyName("data_hora")
    @ServerTimestamp
    var dateTime: Timestamp? = null

)
