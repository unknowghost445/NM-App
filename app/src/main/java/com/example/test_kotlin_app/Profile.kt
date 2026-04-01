package com.example.test_kotlin_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.test_kotlin_app.databinding.ActivityProfileBinding
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    val supabase = Database.SupabaseClient.client

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeProfile()

        binding.btnPExit.setOnClickListener {
            lifecycleScope.launch {
                val intent = Intent(this@Profile, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        binding.btnPSave.setOnClickListener {
            saveProfile()
        }

        binding.btnPMainPage.setOnClickListener{
            startActivity(Intent(this@Profile, MainPage::class.java))
        }
    }

    private fun initializeProfile() {
        binding.progressBar.visibility = View.VISIBLE
        val userAuth = supabase.auth.currentUserOrNull()

        if(userAuth != null) {
            lifecycleScope.launch {
                try{
                    user = supabase.from("users").select{
                        filter {
                            eq("userID", userAuth.id)
                        }
                    }.decodeSingle<User>()

                    binding.progressBar.visibility = View.GONE

                    binding.tvProfile.text = "${user.name}!"

                    binding.etPName.setText(user.name)
                    binding.etPEmail.setText(user.email)
                    binding.etPAddress.setText(user.address)
                    binding.etPAvatarUrl.setText(user.avatarUrl)
                    loadAvatar()
                    binding.etPDescription.setText(user.description)
                }catch (e: Exception){
                    Log.e("Profile", "Error: ${e.message}")
                }finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }else{
            Log.e("Profile", "User not found")
        }
    }

    private fun saveProfile() {
        val userAuth = supabase.auth.currentUserOrNull()

        val name = binding.etPName.text
        val address = binding.etPAddress.text
        val description = binding.etPDescription.text
        val avatarUrl = binding.etPAvatarUrl.text
        var changes = false

        if(userAuth != null) {
            if(user.name  != name.toString().trim()) {
                user.name = name.toString().trim()
                changes = true
            }
            if(user.address != address.toString().trim()) {
                user.address = address.toString().trim()
                changes = true
            }
            if(user.description != description.toString().trim()) {
                user.description = description.toString().trim()
                changes = true
            }
            if(user.avatarUrl != avatarUrl.toString().trim()) {
                user.avatarUrl = avatarUrl.toString().trim()
                loadAvatar()
                changes = true
            }

            if(changes) {
                lifecycleScope.launch {
                    try {
                        supabase.from("users").update(
                            mapOf(
                                "name" to user.name,
                                "address" to user.address,
                                "avatarUrl" to user.avatarUrl,
                                "description" to user.description
                            )
                        ){
                            filter {
                                eq("userID", userAuth.id)
                            }
                        }

                        Toast.makeText(this@Profile, "Update successfully", Toast.LENGTH_SHORT).show()
                    }catch (e: Exception){
                        Log.e("Profile", "Error: ${e.message}")
                    }
                }
            }
        }
    }

    private fun loadAvatar(){
        Glide.with(this)
            .load(binding.etPAvatarUrl.text.toString().trim())
            .into(binding.imageView)
    }
}