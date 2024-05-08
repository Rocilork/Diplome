package com.example.electronicmagazine.Object

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest

object SB {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://eefpcpbldmzljygkugxt.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVlZnBjcGJsZG16bGp5Z2t1Z3h0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDg5NTgxMTksImV4cCI6MjAyNDUzNDExOX0.P0eB4dN0mC-nvLokB-5ZVqw15vG5LiqlwnXXvJzbUbw"
    ) {
        install(GoTrue)
        install(Postgrest)
        //install other modules
    }
    public fun getClient(): SupabaseClient {
        return supabase
    }
}