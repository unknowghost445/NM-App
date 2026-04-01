package com.example.test_kotlin_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.test_kotlin_app.databinding.ActivityLoginBinding
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.providers.builtin.Email


class Login : AppCompatActivity() {

    val supabase = Database.SupabaseClient.client
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setDisableError()

        binding.btnLogin.setOnClickListener {
            val em = binding.etLEmail.text.toString().trim()
            val pass = binding.etLPass.text.toString()

            if(checkValid()) {
                lifecycleScope.launch {
                    try {
                        binding.btnLogin.isEnabled = false

                        supabase.auth.signInWith(Email){
                            email = em
                            password = pass
                        }

                        startActivity(Intent(this@Login, Profile::class.java))
                        finish()
                    }catch (e: Exception){
                        binding.tilLEmail.error = "Invalid email or password"
                        Log.e("Login", "Error: ${e.message}")
                    }finally {
                        binding.btnLogin.isEnabled = true
                    }
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        binding.tvForgotPass.setOnClickListener {
            //TODO: Add forgot pass function
        }
    }

    private fun setDisableError() {
        binding.etLEmail.addTextChangedListener {
            binding.tilLEmail.error = null
        }

        binding.etLPass.addTextChangedListener {
            binding.tilLPass.error = null
        }
    }

    private fun checkValid(): Boolean {
        if(binding.etLEmail.text.toString().trim().isEmpty()){
            binding.tilLEmail.error = "Email cannot be empty"
            return false
        }

        if(binding.etLPass.text.toString().isEmpty()){
            binding.tilLPass.error = "Password cannot be empty"
            return false
        }

        return true
    }
}