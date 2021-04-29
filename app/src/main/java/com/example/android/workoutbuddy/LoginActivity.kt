package com.example.android.workoutbuddy

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.android.workoutbuddy.databinding.ActivityLoginBinding

class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var editText_username: EditText
    private lateinit var editText_password: EditText
    private lateinit var button_login: Button

    private val appViewModel : AppViewModel by viewModels{
        AppViewModelFactory((application as AppApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editText_username = binding.editTextLUsername
        editText_password = binding.editTextLPassword
        button_login = binding.buttonLSignup


        button_login.setOnClickListener {
            if (TextUtils.isEmpty(editText_username.text.toString()) || TextUtils.isEmpty(editText_password.text.toString())){
                Toast.makeText(this, "Missing Fields", Toast.LENGTH_SHORT).show()
            }
            else{
                appViewModel.getUser(editText_username.text.toString()).observe(this, Observer {
                    user -> user?.let {
                        if (user.title == editText_username.text.toString() && user.content == editText_password.text.toString().toInt()){
                            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                }
                })
            }
        }
    }

}