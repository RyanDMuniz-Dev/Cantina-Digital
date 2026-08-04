package com.example.cantinadigital.data.repository

import com.example.cantinadigital.data.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class OrderRepository (
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val path: String = "pedidos"
) {

    fun GetOrderFlow() : Flow<List<Order>>

}