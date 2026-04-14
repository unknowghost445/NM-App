package com.example.test_kotlin_app

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

object Database {
    object SupabaseClient {
        val client = createSupabaseClient(
            supabaseUrl = "https://tdqoqnnbdgayqdmeaadr.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRkcW9xbm5iZGdheXFkbWVhYWRyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQ5MDQyODgsImV4cCI6MjA5MDQ4MDI4OH0.lx8XEVXyVL77FwZdN1S2oInKeu9L6q0Fq3Covf9-Xpw"
        ){
            install(io.github.jan.supabase.auth.Auth)
            install(Postgrest)
        }
    }

    object PostItems {
        private val supabase = SupabaseClient.client

        suspend fun getPostItemsByUser(): List<Post> {
            val userID = supabase.auth.currentUserOrNull()?.id ?: return emptyList()

            return SupabaseClient.client.from("postdata").select().decodeList<Post>()
        }

        suspend fun post(data: String) {
            val userID = supabase.auth.currentUserOrNull()?.id ?: return

            SupabaseClient.client.from("postdata").insert(
                mapOf(
                    "userID" to userID,
                    "data" to data
                )
            )
        }
    }

    object Auth{
        private val supabase = SupabaseClient.client

        suspend fun signIn(email: String, password: String){
            supabase.auth.signInWith(Email){
                this.email = email
                this.password = password
                }
        }

        suspend fun register(name: String, email: String, password: String): Result<Unit> {
            return try{
                supabase.auth.signUpWith(Email){
                    this.email = email
                    this.password = password
                }
                val user = supabase.auth.currentUserOrNull()

                if(user != null){
                    supabase.from("users").insert(
                        mapOf(
                            "userID" to user.id,
                            "name" to name,
                            "email" to email,
                            "address" to "",
                            "avatarUrl" to "",
                            "description" to ""
                        )
                    )
                }
                Result.success(Unit)

            }catch (e: Exception){
                Result.failure(e)
            }
        }
    }

    object UserRepo{
        private val supabase = SupabaseClient.client

        suspend fun getUser(): User?{
            val userID = supabase.auth.currentUserOrNull()?.id ?: return null

            return supabase.from("users").select {
                filter {
                    eq("userID", userID)
                }
            }.decodeSingle<User>()
        }
    }
}