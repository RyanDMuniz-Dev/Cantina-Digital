package com.example.cantinadigital.data.repository

import com.example.cantinadigital.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class AuthRepository {

    private val client = SupabaseClient.client

    suspend fun signUp(name: String, sClass: String, userEmail: String, pass: String) {

        val user = client.auth.signUpWith(Email) {
            email = userEmail
            password = pass
        }

        if (user != null) {
            client.from("profiles").insert(mapOf(
                "id" to user.id,
                "full_name" to name,
                "student_class" to sClass
            ))
        }

    }

}