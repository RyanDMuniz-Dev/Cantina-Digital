package com.example.cantinadigital.data.repository

import com.example.cantinadigital.data.model.Order
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    private val ordersPath: String = "pedidos"
    private val productsPath: String = "produtos"

    fun getOrderFlow(): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection(ordersPath)
            .orderBy("data_hora", Query.Direction.DESCENDING)
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

    suspend fun createOrder(order: Order): Boolean {
        return try {
            firestore.runTransaction { transaction ->
                // 1º PASSO: Fazer TODAS as leituras (transaction.get) primeiro
                val updates = mutableListOf<Pair<com.google.firebase.firestore.DocumentReference, Long>>()

                for (item in order.items) {
                    if (item.productId.isNotBlank()) {
                        val productRef = firestore.collection(productsPath).document(item.productId)
                        val snapshot = transaction.get(productRef)

                        if (snapshot.exists()) {
                            val currentAmount = snapshot.getLong("quantidade")
                                ?: snapshot.getLong("quantidadeEstoque")
                                ?: 0L
                            val newAmount = (currentAmount - item.amount).coerceAtLeast(0)
                            updates.add(Pair(productRef, newAmount))
                        }
                    }
                }

                // 2º PASSO: Fazer TODAS as escritas (transaction.update e transaction.set) no final
                for ((ref, newAmount) in updates) {
                    transaction.update(ref, "quantidade", newAmount)
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

    suspend fun deleteOrder(order: Order): Boolean {
        return try {
            firestore.collection(ordersPath).document(order.id).delete().await()

            // Devolve o estoque de cada item
            for (item in order.items) {
                if (item.productId.isNotBlank()) {
                    val productRef = firestore.collection(productsPath).document(item.productId)
                    firestore.runTransaction { transaction ->
                        val snapshot = transaction.get(productRef)
                        if (snapshot.exists()) {
                            val currentStock = snapshot.getLong("quantidade")
                                ?: snapshot.getLong("quantidadeEstoque")
                                ?: 0L
                            transaction.update(productRef, "quantidade", currentStock + item.amount)
                        }
                    }.await()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}