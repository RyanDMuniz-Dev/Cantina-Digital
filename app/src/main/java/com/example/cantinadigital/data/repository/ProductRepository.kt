package com.example.cantinadigital.data.repository

import com.example.cantinadigital.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ProductRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val path: String = "produtos"
) {

    fun getProductFlow(): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection(path)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val products = snapshot.toObjects(Product::class.java)
                    trySend(products)
                }
            }

        awaitClose { listener.remove() }
    }

    fun addProduct(product: Product, onResult: (Boolean) -> Unit) {
        firestore.collection(path)
            .add(product)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun updateProduct(product: Product, onResult: (Boolean) -> Unit) {

        if (product.id.isBlank()) {
            onResult(false)
            return
        }

        firestore.collection(path)
            .document(product.id)
            .set(product)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }

    }

    fun deleteProduct(product: Product, onResult: (Boolean) -> Unit) {

        if (product.id.isBlank()) {
            onResult(false)
            return
        }

        firestore.collection(path)
            .document(product.id)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }

    }

}