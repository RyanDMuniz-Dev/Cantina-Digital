package com.example.cantinadigital.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {

    // Instância global do Firebase Auth para gerenciar logins e cadastros
    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    // Instância global do Cloud Firestore para acessar o banco de dados
    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}