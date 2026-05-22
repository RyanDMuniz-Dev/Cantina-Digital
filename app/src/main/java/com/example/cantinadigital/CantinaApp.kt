package com.example.cantinadigital

import android.app.Application
import com.example.cantinadigital.data.remote.SupabaseClientProvider

class CantinaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SupabaseClientProvider.getClient(this)
    }
}