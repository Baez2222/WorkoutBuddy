package com.example.android.workoutbuddy

import android.app.ActionBar
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.android.workoutbuddy.databinding.ActivityCreateworkoutBinding

class CreateWorkoutActivity: AppCompatActivity() {

    private lateinit var binding : ActivityCreateworkoutBinding
    private lateinit var editText_workoutName: EditText
    private lateinit var button_add: Button
    private lateinit var button_submit: Button
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateworkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editText_workoutName = binding.editTextWorkoutName
        button_add = binding.buttonAddExercise
        button_submit = binding.buttonSubmit
        linearLayout = binding.linearLayoutExercises


        button_add.setOnClickListener {
            val horizontalLayout = LinearLayout(this)
            horizontalLayout.orientation = LinearLayout.HORIZONTAL

            linearLayout.addView(horizontalLayout, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))

            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.weight = 1F

            val layoutParams_ex = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams_ex.weight = 0.29F

            val editText_exercise = EditText(this)
            editText_exercise.hint = "Exercise Name"
            editText_exercise.textSize = 14F
            editText_exercise.layoutParams = layoutParams_ex

            val editText_sets = EditText(this)
            editText_sets.inputType = InputType.TYPE_CLASS_NUMBER
//            editText_sets.setEms(10)
            editText_sets.layoutParams = layoutParams

            val editText_reps = EditText(this)
            editText_reps.inputType = InputType.TYPE_CLASS_NUMBER
//            editText_reps.setEms(10)
            editText_reps.layoutParams = layoutParams

            val editText_rest = EditText(this)
            editText_rest.inputType = InputType.TYPE_CLASS_NUMBER
//            editText_rest.setEms(10)
            editText_rest.layoutParams = layoutParams

//            horizontalLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            horizontalLayout.addView(editText_exercise)
            horizontalLayout.addView(editText_sets)
            horizontalLayout.addView(editText_reps)
            horizontalLayout.addView(editText_rest)
        }
    }
}