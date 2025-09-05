package com.example.issuespotter.clients

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseManager {

    var supabaseClient: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://rvmbnjbuzpfgpiyxufdw.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJ2bWJuamJ1enBmZ3BpeXh1ZmR3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTQ0NzAyNzUsImV4cCI6MjA3MDA0NjI3NX0.37wK43D-kctT8YyEIhaI7TChux90hTp1BK9zMuxWIas"
    ) {
        install(Auth)
        install(Storage)
        install(Postgrest)
    }
}