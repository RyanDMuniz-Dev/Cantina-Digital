package com.example.cantinadigital.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = "https://ssuyvdvucoejicoacfsu.supabase.co",
        supabaseKey = "sb_publishable_oG1ta5oT2BkTP6hOa7312w_7sj2tkOW"
    ) {
        install(Auth)
        install(Postgrest)
    }

}