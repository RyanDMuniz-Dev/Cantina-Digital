package com.example.cantinadigital.data.model

import com.google.firebase.firestore.PropertyName

data class PayoutProduct(

    /*
     * Identificação interna.
     * NÃO precisa aparecer para o usuário.
     */
    @get:PropertyName("pedido_id")
    @set:PropertyName("pedido_id")
    var pedidoId: String = "",

    @get:PropertyName("produto_id")
    @set:PropertyName("produto_id")
    var produtoId: String = "",

    @get:PropertyName("nome")
    @set:PropertyName("nome")
    var nome: String = "",

    @get:PropertyName("quantidade")
    @set:PropertyName("quantidade")
    var quantidade: Int = 0,

    @get:PropertyName("valor_unitario")
    @set:PropertyName("valor_unitario")
    var valorUnitario: Double = 0.0,

    @get:PropertyName("valor_total")
    @set:PropertyName("valor_total")
    var valorTotal: Double = 0.0,

    @get:PropertyName("taxa_cantina")
    @set:PropertyName("taxa_cantina")
    var taxaCantina: Double = 0.0

)