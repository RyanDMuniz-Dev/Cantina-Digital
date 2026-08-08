package com.example.cantinadigital.data.repository

import com.example.cantinadigital.data.model.FinancialTransaction
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FinancialRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val transactionPath: String = "movimentacoes"
) {

    fun getTransactions(): Flow<List<FinancialTransaction>> = callbackFlow {
        val listener = firestore.collection("")
            .orderBy("data_hora", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val transactions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FinancialTransaction::class.java)
                } ?: emptyList()

                trySend(transactions)

            }
    }

    suspend fun addTransaction(transaction: FinancialTransaction): Boolean {
        return try {
            val docRef = firestore.collection(transactionPath).document()
            val transactionWithDetails = transaction.copy(
                id = docRef.id,
                dataHora = Timestamp.now()
            )
            docRef.set(transactionWithDetails).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}