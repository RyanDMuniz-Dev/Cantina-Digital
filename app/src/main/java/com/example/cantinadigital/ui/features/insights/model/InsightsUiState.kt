package com.example.cantinadigital.ui.features.insights.model

import com.example.cantinadigital.data.model.Payout

data class InsightsUiState(
    val totalRevenue: Double = 0.0,
    val cantinaRoyalties: Double = 0.0,
    val totalExits: Double = 0.0,
    val totalBalance : Double = 0.0,
    val sellersRoyalties: List<SellerPayoutSummary> = emptyList(),
    val confirmedPayouts: List<Payout> = emptyList(),
    val isLoading: Boolean = true,
    val isProcessingPayout: Boolean = false,
    val payoutError: String? = null
)
