package com.example.cantinadigital.data.repository

import com.example.cantinadigital.data.model.Payout
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

class PayoutRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val payoutPath: String = "repasses",
    private val controlPath: String = "repasses_controle"
) {

    fun getPayouts(): Flow<List<Payout>> = callbackFlow {

        val listener = firestore
            .collection(payoutPath)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val payouts = snapshot
                    ?.documents
                    ?.mapNotNull { document ->
                        document.toObject(Payout::class.java)
                    }
                    ?: emptyList()

                trySend(payouts)
            }

        awaitClose {
            listener.remove()
        }
    }

    suspend fun createPayout(payout: Payout): Result<Unit> {

        return try {

            require(payout.vendedor.isNotBlank()) {
                "Vendedor inválido."
            }

            require(payout.produtos.isNotEmpty()) {
                "Não há produtos para este repasse."
            }

            require(payout.valorRepassado > 0.0) {
                "O valor do repasse deve ser maior que zero."
            }

            /*
             * Uma chave representa:
             *
             * PEDIDO + VENDEDOR
             *
             * Isso é importante porque um mesmo pedido pode
             * conter produtos de João e Maria.
             */
            val paidKeys = payout.produtos
                .map {
                    createPayoutKey(
                        orderId = it.pedidoId,
                        sellerName = payout.vendedor
                    )
                }
                .distinct()

            val controlDocumentId =
                sha256(
                    payout.vendedor
                        .trim()
                        .lowercase()
                )

            val controlRef = firestore
                .collection(controlPath)
                .document(controlDocumentId)

            /*
             * O ID do histórico é determinístico.
             *
             * Mesmo conjunto de produtos/pedidos
             * sempre gera o mesmo ID.
             */
            val payoutDocumentId = sha256(
                payout.vendedor.trim().lowercase() +
                        "|" +
                        paidKeys.sorted().joinToString("|")
            )

            val payoutRef = firestore
                .collection(payoutPath)
                .document(payoutDocumentId)

            firestore.runTransaction { transaction ->

                /*
                 * 1. LEITURAS
                 *
                 * Nunca fazemos uma escrita antes das leituras
                 * dentro da transação.
                 */
                val controlSnapshot =
                    transaction.get(controlRef)

                val alreadyPaid =
                    controlSnapshot
                        .get("paid_keys") as? List<*>
                        ?: emptyList<Any>()

                val duplicatedKeys =
                    paidKeys.filter { key ->
                        key in alreadyPaid
                    }

                if (duplicatedKeys.isNotEmpty()) {
                    throw PayoutAlreadyConfirmedException(
                        "Um ou mais produtos desta venda já foram repassados."
                    )
                }

                /*
                 * 2. GRAVAÇÃO DO HISTÓRICO
                 */
                transaction.set(
                    payoutRef,
                    payout.copy(
                        id = payoutDocumentId
                    )
                )

                /*
                 * 3. MARCA COMO PAGO
                 *
                 * ArrayUnion adiciona as chaves sem duplicá-las.
                 */
                if (controlSnapshot.exists()) {

                    transaction.update(
                        controlRef,
                        mapOf(
                            "paid_keys" to FieldValue.arrayUnion(
                                *paidKeys.toTypedArray()
                            ),
                            "updated_at" to FieldValue.serverTimestamp()
                        )
                    )

                } else {

                    transaction.set(
                        controlRef,
                        mapOf(
                            "vendedor" to payout.vendedor,
                            "paid_keys" to paidKeys,
                            "updated_at" to FieldValue.serverTimestamp()
                        )
                    )
                }

                null
            }.await()

            Result.success(Unit)

        } catch (e: PayoutAlreadyConfirmedException) {

            Result.failure(e)

        } catch (e: Exception) {

            e.printStackTrace()

            Result.failure(e)
        }
    }

    private fun createPayoutKey(
        orderId: String,
        sellerName: String
    ): String {

        return "${orderId}|${sellerName.trim().lowercase()}"
    }

    private fun sha256(value: String): String {

        val digest =
            MessageDigest.getInstance("SHA-256")

        val hash =
            digest.digest(value.toByteArray())

        return hash.joinToString("") {
            "%02x".format(it)
        }
    }
}

class PayoutAlreadyConfirmedException(
    message: String
) : Exception(message)