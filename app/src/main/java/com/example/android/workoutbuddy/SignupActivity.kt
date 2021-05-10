package com.example.android.workoutbuddy

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
import com.example.android.workoutbuddy.databinding.ActivitySignupBinding
import java.io.IOException
import java.io.InputStream

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var editText_username: EditText
    private lateinit var editText_password: EditText
    private lateinit var editText_rpassword: EditText
//    private lateinit var editText_email: EditText
    private lateinit var button_signup: Button
    private lateinit var imageView_logo: ImageView


    private val appViewModel : AppViewModel by viewModels{
        AppViewModelFactory((application as AppApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editText_username = binding.editTextSUsername
        editText_password = binding.editTextSPassword
        editText_rpassword = binding.editTextSReenterPassword
//        editText_email = binding.editTextSEmail
        button_signup = binding.buttonSSignup
        imageView_logo= binding.imageViewSignup


        button_signup.setOnClickListener {
            if (TextUtils.isEmpty(editText_password.text) || TextUtils.isEmpty(editText_rpassword.text) || TextUtils.isEmpty(editText_username.text)){
                Toast.makeText(this, "Missing Fields", Toast.LENGTH_SHORT).show()
            }
            else if (editText_password.text.toString() != editText_rpassword.text.toString()){
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            }
            else{
                var created = 0
                appViewModel.isUsernameTaken(editText_username.text.toString()).observe(this, Observer {
                    if (it > 0){
                        if ( created == 0){
                            Toast.makeText(this, "Username already in use.", Toast.LENGTH_SHORT).show()
                            created = 0
                        }
                    }
                    else if (it == 0){
                        val user = User(editText_username.text.toString(), editText_password.text.toString().toInt())
                        appViewModel.insert(user)
                        Toast.makeText(this, "Account created", Toast.LENGTH_LONG).show()
                        created = 1
                        finish()
                    }
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