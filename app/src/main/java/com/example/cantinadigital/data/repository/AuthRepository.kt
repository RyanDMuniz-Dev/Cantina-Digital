package com.example.cantinadigital.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

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
        val uid = auth.currentUser?.uid
            ?: return Result.failure(Exception("Nenhum usuário logado."))

        return try {
            println("DEBUG UID: $uid")

            val snapshot = db
                .collection("users")
                .document(uid)
                .get()
                .await()

            println("DEBUG USER EXISTS: ${snapshot.exists()}")
            println("DEBUG USER DATA: ${snapshot.data}")

            Result.success(snapshot.data)
        } catch (e: Exception) {
            println("DEBUG USER ERROR: ${e.message}")
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}