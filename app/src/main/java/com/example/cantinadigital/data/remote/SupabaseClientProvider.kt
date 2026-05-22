package com.example.cantinadigital.data.remote

import android.content.Context
import com.example.cantinadigital.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.SettingsSessionManager

object SupabaseClientProvider {

    private var _client: io.github.jan.supabase.SupabaseClient? = null
    val client get() = _client ?: error("SupabaseClient não inicializado. Certifique-se de chamar getClient() primeiro.")

    fun getClient(context: Context) = _client ?: createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth) {
            flowType = FlowType.PKCE
            scheme = "cantinadigital"
            host = "login"
            sessionManager = SettingsSessionManager()
        }
        install(Postgrest)
    }.also { _client = it }

}