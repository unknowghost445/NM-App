package com.example.test_kotlin_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.test_kotlin_app.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPrefs = UserPreferences(this)
        lifecycleScope.launch {
            userPrefs.isLoggedIn.collect {
                loggedIn -> if(loggedIn){
                    startActivity(Intent(this@Login, Profile::class.java))
                    finish()
                }
            }
        }

        setDisableError()

        binding.btnLogin.setOnClickListener {
            val email = binding.etLEmail.text.toString().trim()
            val pass = binding.etLPass.text.toString()

            if(checkValid()) {
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(this) {
                        lifecycleScope.launch {
                            userPrefs.saveLoginStatus(true)
                            startActivity(Intent(this@Login, Profile::class.java))
                            finish()
                        }
                    }
                    .addOnFailureListener(this){
                        binding.tilLPass.error = "Invalid email or password"
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