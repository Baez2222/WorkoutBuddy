package com.example.android.workoutbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.android.workoutbuddy.databinding.ActivityHomeBinding

class HomeActivity: AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding
    private lateinit var button_startWorkout: Button
    private lateinit var button_calorieTracker: Button
    private lateinit var button_createWorkout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        button_startWorkout = binding.buttonStartWorkout
        button_calorieTracker = binding.buttonCalorieTracker
        button_createWorkout = binding.buttonCreateWorkout


        button_createWorkout.setOnClickListener {
            val intent = Intent(this, CreateWorkoutActivity::class.java)
            startActivity(intent)
        }
    }
}