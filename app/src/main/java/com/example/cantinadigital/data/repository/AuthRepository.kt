package com.example.cantinadigital.data.repository

import com.example.cantinadigital.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class Profile (
    val id: String? = null,
    val full_name: String,
    val student_class: String
)

class AuthRepository {

    private val client = SupabaseClientProvider.client

    suspend fun signUp(
        fullName: String,
        studentClass: String,
        email: String,
        password: String
    ): Result<Unit> = runCatching {

        // Cria a conta do usuario
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        // Faz login automatico para obter a sessão
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        // Armazena o valor da sessão atual
        val userId = client.auth.currentUserOrNull()?.id
            ?: error("Usuário não encontrado após cadastro")

        println("USER ID: $userId")

        // Da INSERT na tabela "profile" no supabase
        client.postgrest["profiles"].insert(
            Profile(
                id = userId,
                full_name = fullName,
                student_class = studentClass
            )
        )
    }

    suspend fun signIn(
        email: String,
        password: String
    ) : Result<Unit> = runCatching {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

}