package com.example.issuespotter.clients

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseManager {

    var supabaseClient: SupabaseClient = createSupabaseClient(
        //supabaseUrl = "https://rvmbnjbuzpfgpiyxufdw.supabase.co",
        //supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJ2bWJuamJ1enBmZ3BpeXh1ZmR3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTQ0NzAyNzUsImV4cCI6MjA3MDA0NjI3NX0.37wK43D-kctT8YyEIhaI7TChux90hTp1BK9zMuxWIas"
        supabaseUrl = "https://sfwyvjgppmozsuebiqok.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNmd3l2amdwcG1venN1ZWJpcW9rIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQwMTU1NTcsImV4cCI6MjA4OTU5MTU1N30.s8gSnall0hPJ5t4_pRbMfGi4v7GiY-fjv5CwylJ_hno"

    ) {
        install(Auth)
        install(Storage)
        install(Postgrest)
    }
}