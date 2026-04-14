package com.example.test_kotlin_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.test_kotlin_app.databinding.ActivityMainPageBinding
import kotlinx.coroutines.launch

class MainPage : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initialize()

        binding.btnMPost.setOnClickListener {
            lifecycleScope.launch {
                Database.PostItems.post(binding.etMPost.text.toString().trim())
                initialize()
                binding.etMPost.setText("")
            }
        }

        binding.btnMProfile.setOnClickListener {
            startActivity(Intent(this@MainPage, Profile::class.java))
        }
    }

    private fun initialize(){
        lifecycleScope.launch {
            val listView = binding.lvMView
            val data = Database.PostItems.getPostItemsByUser()
            val usersById = Database.UserRepo.getAllUsers().associateBy { it.userID }

            val adapter = ItemAdapter(this@MainPage, R.layout.item_post, data, usersById)
            listView.adapter = adapter
        }
    }
}
