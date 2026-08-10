package com.example.cantinadigital.ui.features.insights.model

data class SellerPayoutSummary(
    val sellerName : String,
    val weekLabel: String = "Semana atual",
    val isPaid: Boolean = false,
    val itemsSold: List<Pair<String, Int>>,
    val grossTotal: Double,
    val cantinaTax: Double,
    val liquidValueRepass: Double,
    val orderIdsToConfirm: List<String> = emptyList()
)
