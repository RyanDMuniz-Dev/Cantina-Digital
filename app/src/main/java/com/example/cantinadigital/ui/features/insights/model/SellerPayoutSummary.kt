package com.example.cantinadigital.ui.features.insights.model

import com.example.cantinadigital.data.model.PayoutProduct

data class SellerPayoutSummary(

    val sellerName: String,

    val statusLabel: String = "Repasse pendente",

    val isPaid: Boolean = false,

    /*
     * Continua útil para a UI compacta.
     */
    val itemsSold: List<Pair<String, Int>> = emptyList(),

    val grossTotal: Double = 0.0,

    val cantinaTax: Double = 0.0,

    val liquidValueRepass: Double = 0.0,

    /*
     * Snapshot que será gravado quando confirmar.
     */
    val payoutProducts: List<PayoutProduct> = emptyList()
)