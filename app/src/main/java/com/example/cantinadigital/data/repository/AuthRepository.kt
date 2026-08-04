package com.example.cantinadigital.data.repository

import com.example.cantinadigital.data.remote.FirebaseProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseProvider.auth
    private val db = FirebaseProvider.db

    fun getUsuarioAtual(): FirebaseUser? {
        return auth.currentUser
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Função de Login (bifurcada com nome signIn para alinhar com o LoginScreenViewModel)
     */
    suspend fun signIn(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Função de Cadastro (alinhada com os parâmetros fullName e studentClass do SignUpScreenViewModel)
     */
    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
        studentClass: String
    ): Result<Boolean> {
        return try {
            // 1. Cria autenticação no Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Erro ao obter ID do usuário.")

            // 2. Monta objeto de dados para o Firestore
            val userProfile = hashMapOf(
                "uid" to userId,
                "nome" to fullName,
                "email" to email,
                "turma" to studentClass
            )

            // 3. Salva no Firestore na coleção "users"
            db.collection("users").document(userId).set(userProfile).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Puxa os dados do perfil logado (nome, turma)
     */
    suspend fun getDadosUsuarioLogado(): Result<Map<String, Any>?> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Nenhum usuário logado."))

        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            Result.success(snapshot.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}