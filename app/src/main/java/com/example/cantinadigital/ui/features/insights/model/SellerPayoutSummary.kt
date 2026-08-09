package com.example.cantinadigital.ui.features.insights.model

data class SellerPayoutSummary(
    val sellerName : String,
    val itemsSold: List<Pair<String, Int>>,
    val grossTotal: Double,
    val cantinaTax: Double,
    val liquidValueRepass: Double
)
