package com.example.android.workoutbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.android.workoutbuddy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var buttonLogin: Button
    private lateinit var buttonSignup: Button
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        buttonLogin = binding.buttonLogin
        buttonSignup = binding.buttonSignup

        buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        buttonSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}