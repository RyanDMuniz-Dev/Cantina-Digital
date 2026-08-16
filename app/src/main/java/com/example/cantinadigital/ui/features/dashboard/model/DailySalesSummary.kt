package com.example.cantinadigital.ui.features.dashboard.model

import java.time.LocalDate

data class DailySalesSummary(
    val date: LocalDate,
    val label: String,
    val revenue: Double = 0.0
)