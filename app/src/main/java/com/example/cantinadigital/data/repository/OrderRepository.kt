package com.example.cantinadigital.data.repository

import com.example.cantinadigital.data.model.Order
import com.google.android.gms.common.api.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class OrderRepository (
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val path: String = "pedidos"
) {

    fun getOrderFlow(): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection(path)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val orders = snapshots.toObjects(Order::class.java)
                    trySend(orders)
                }
            }

        awaitClose { listener.remove() }
    }

    fun addOrder(order: Order, onResult: (Boolean) -> Unit) {
        firestore.collection(path)
            .add(order)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }


}