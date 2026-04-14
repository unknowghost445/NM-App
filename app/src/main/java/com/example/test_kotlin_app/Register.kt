package com.example.test_kotlin_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.test_kotlin_app.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {

    val supabase = Database.SupabaseClient.client

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setDisableError()

        binding.btnRegister.setOnClickListener {
            if(checkValidRegister()) {
                val name = binding.etRName.text.toString().trim()
                val em = binding.etREmail.text.toString().trim()
                val pass = binding.etRPass.text.toString()

                lifecycleScope.launch {
                    binding.btnRegister.isEnabled = false

                     val result = Database.Auth.register(name, em, pass)

                    result.onSuccess{
                        Toast.makeText(this@Register, "Register successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Register, Login::class.java))
                        finish()
                    }

                    result.onFailure {e ->
                        if (e.message?.contains("User already registered") == true) {
                            binding.etREmail.error = "Email already exists"
                        } else {
                            Log.e("Register", "Error: ${e.message}")
                            e.printStackTrace()
                        }
                    }

                    binding.btnRegister.isEnabled = true
                }
            }
        }

        binding.tvRLogin.setOnClickListener {
            startActivity(Intent(this@Register, Login::class.java))
        }
    }

    private fun setDisableError() {
        val name = binding.etRName
        val email = binding.etREmail
        val pass = binding.etRPass
        val confirmPass = binding.etRConfirmPass

        name.addTextChangedListener {
            name.error = null
        }
        email.addTextChangedListener {
            email.error = null
        }
        pass.addTextChangedListener {
            pass.error = null
        }
        confirmPass.addTextChangedListener {
            confirmPass.error = null
        }
    }

    private fun checkValidRegister(): Boolean {
        var flag = true

        val name = binding.etRName.text.toString().trim()
        val email = binding.etREmail.text.toString().trim()
        val pass = binding.etRPass.text.toString()
        val confirmPass = binding.etRConfirmPass.text.toString()

        if (name.isEmpty()) {
            binding.etRName.error = "Name cannot be empty"
            flag = false
        }

        if(!(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            binding.etREmail.error = "Invalid email format"
            flag = false
        }

        if (email.isEmpty()) {
            binding.etREmail.error = "Email cannot be empty"
            flag = false
        }

        if(pass.length < 5) {
            binding.etRPass.error = "Password must be at least 5 characters"
            flag = false
        }

        if (pass.isEmpty()) {
            binding.etRPass.error = "Password cannot be empty"
            flag = false
        }

        if (confirmPass.isEmpty()) {
            binding.etRConfirmPass.error = "Confirm password cannot be empty"
            flag = false
        } else if (pass != confirmPass) {
            binding.etRConfirmPass.error = "Passwords do not match"
            flag = false
        }

        return flag
    }
}