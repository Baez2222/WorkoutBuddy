package com.example.android.workoutbuddy

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.example.android.workoutbuddy.databinding.ActivityMainBinding
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {

//    private lateinit var buttonLogin: Button
//    private lateinit var buttonSignup: Button
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageView: ImageView
    private lateinit var fragContainer: FragmentContainerView
    private lateinit var fragmentManager: FragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
//        buttonLogin = binding.buttonLogin
//        buttonSignup = binding.buttonSignup
        imageView = binding.imageViewLogo
        fragContainer = binding.fragMainContainer

//        buttonSignup.setOnClickListener {
//            val intent = Intent(this, SignupActivity::class.java)
//            startActivity(intent)
//        }

        // set logo
        var inputStream: InputStream? = null
        try {
            inputStream = assets.open("free-logo.png")
            val brew = Drawable.createFromStream(inputStream, null)
            imageView.setImageDrawable(brew)
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val mainFragment = FragmentMain()
        loadFragment(mainFragment, fragContainer.id, "MainFragment")
    }


    private fun loadFragment(fragment: Fragment, id: Int, tag: String) {
        // FragmentManager
        // API 28 -> accessibility of this manager
        // before api 28, getFragmentManager()
        // after, getSupportFragmentManager()

        // create a fragment manager
        fragmentManager = supportFragmentManager
        // create a fragment transaction to begin the transaction and replace the fragment
        val fragmentTransaction = fragmentManager.beginTransaction()
        // replacing the placeholder - fragmentContainerView with the fragment that is passed as parameter
        fragmentTransaction.replace(id, fragment, tag)
        fragmentTransaction.commit()
    }
}