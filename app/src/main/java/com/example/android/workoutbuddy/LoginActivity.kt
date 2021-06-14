package com.example.android.workoutbuddy

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.android.workoutbuddy.database.AppApplication
import com.example.android.workoutbuddy.database.AppViewModel
import com.example.android.workoutbuddy.database.AppViewModelFactory
import com.example.android.workoutbuddy.databinding.ActivityLoginBinding
import java.io.IOException
import java.io.InputStream

class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var editText_username: EditText
    private lateinit var editText_password: EditText
    private lateinit var button_login: Button
    private lateinit var imageView_logo: ImageView

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
        imageView_logo = binding.imageViewLogin


        button_login.setOnClickListener {
            if (TextUtils.isEmpty(editText_username.text.toString()) || TextUtils.isEmpty(editText_password.text.toString())){
                Toast.makeText(this, "Missing Fields", Toast.LENGTH_SHORT).show()
            }
            else{
                appViewModel.getUser(editText_username.text.toString()).observe(this, Observer { user ->
//                    user -> user?.let {
                    if(user == null){
                        Toast.makeText(this, "Invalid Information Entered", Toast.LENGTH_SHORT).show()
                    }
                    else if (user.title == editText_username.text.toString() && user.content == editText_password.text.toString().toInt()){
                        Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("username", user.title)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this, "Invalid Information Entered", Toast.LENGTH_SHORT).show()
                        }
//                }
                })
            }
        }

        // set logo
        var inputStream: InputStream? = null
        try {
            inputStream = assets.open("free-logo.png")
            val brew = Drawable.createFromStream(inputStream, null)
            imageView_logo.setImageDrawable(brew)
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}