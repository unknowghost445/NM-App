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
import com.example.test_kotlin_app.databinding.ActivityProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeProfile()

        binding.btnPExit.setOnClickListener {
            val userPrefs = UserPreferences(this)
            lifecycleScope.launch {
                userPrefs.clearLoginStatus()

                val intent = Intent(this@Profile, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        binding.btnPSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun initializeProfile() {
        binding.progressBar.visibility = View.VISIBLE
        val userID = auth.currentUser?.uid
        val db = Firebase.firestore

        if(userID != null) {
            db.collection("users").document(userID).get()
                .addOnSuccessListener { document ->
                    binding.progressBar.visibility = View.GONE

                    if(document != null && document.exists()) {
                        user = document.toObject(User::class.java)!!

                        if(user != null) {
                            binding.etPName.setText(user.name)
                            binding.etPEmail.setText(user.email)
                            binding.etPAddress.setText(user.address)
                            binding.etPDescription.setText(user.description)
                        }else {
                            Log.d("Profile", "User object is null")
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("Profile", "Error getting user document")
                }
        }
    }

    private fun saveProfile() {
        val userID = auth.currentUser?.uid
        val db = Firebase.firestore

        val name = binding.etPName.text
        val address = binding.etPAddress.text
        val description = binding.etPDescription.text
        var changes = false

        if(userID != null && user != null) {
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

            if(changes) {
                db.collection("users").document(userID).set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Log.e("Profile", "Error updating profile")
                    }
            }
        }
    }
}