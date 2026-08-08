package com.example.cantinadigital.data.repository

import com.example.cantinadigital.data.model.Order
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OrderRepository (
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val ordersPath: String = "pedidos",
    private val productsPath: String = "produtos"
) {

    fun getOrderFlow(): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection(ordersPath)
            .orderBy("data_hora", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Order::class.java)
                } ?: emptyList()

                trySend(orders)
            }

        awaitClose { listener.remove() }
    }

    suspend fun createOrder(order: Order) : Boolean {
        return try {
            firestore.runTransaction { transaction ->
                for (item in order.items) {
                    if (item.productId.isNotBlank()) {
                        val productRef = firestore.collection(productsPath).document(item.productId)
                        val snapshot = transaction.get(productRef)

                        val currentAmount = snapshot.getLong("quantidade") ?: 0L
                        val newAmount = (currentAmount - item.amount).coerceAtLeast(0)

                        transaction.update(productRef, "quantidade", newAmount)
                    }
                }

                val newOrderRef = firestore.collection(ordersPath).document()
                val orderWithDetails = order.copy(
                    id = newOrderRef.id,
                    dateTime = Timestamp.now()
                )

                transaction.set(newOrderRef, orderWithDetails)
            }.await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}