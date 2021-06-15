package com.example.android.workoutbuddy

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.example.android.workoutbuddy.database.*
import com.example.android.workoutbuddy.databinding.ActivityUpdateworkoutBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zerobranch.layout.SwipeLayout
import java.lang.reflect.Type

class UpdateWorkoutActivity: AppCompatActivity() {

    private lateinit var binding: ActivityUpdateworkoutBinding
    private lateinit var buttonSubmit: Button
    private lateinit var buttonAdd: Button
    private lateinit var workoutNameText: EditText
    private lateinit var linearLayout: LinearLayout

    private lateinit var username: String
    private lateinit var workoutName: String


    private val appViewModel : AppViewModel by viewModels{
        AppViewModelFactory((application as AppApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdateworkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttonSubmit = binding.buttonSubmit2
        buttonAdd = binding.buttonAddExercise2
        workoutNameText = binding.editTextWorkoutName2
        linearLayout = binding.linearLayoutExercises2

        username = intent.getStringExtra("username").toString()
        workoutName = intent.getStringExtra("workout").toString()


        workoutNameText.setText(workoutName)
        workoutNameText.isFocusable = false
        workoutNameText.setTextColor(Color.GRAY)

        buttonAdd.setOnClickListener {
            val inflater = LayoutInflater.from(applicationContext)
            val view= inflater.inflate(R.layout.exercise_item, null)
            val deleteView: TextView = view.findViewById(R.id.right_view)
            linearLayout.addView(view, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
            deleteView.setOnClickListener {
                linearLayout.removeView(view)
            }
        }

        buttonSubmit.setOnClickListener {
            if (linearLayout.childCount == 0){
                Toast.makeText(this, "Exercise missing", Toast.LENGTH_SHORT).show()
            }
            else{
                var foundEmpty = false
                for ( i in 0..linearLayout.childCount){
                    if (foundEmpty){ break }
                    val childLayout = linearLayout.getChildAt(i)
                    if ( childLayout is ConstraintLayout){
                        val currLL = (childLayout.getChildAt(0) as SwipeLayout).getChildAt(1) as LinearLayout
                        for (j in 0..currLL.childCount){
                            val childLL = currLL.getChildAt(j) as? EditText
                            if(childLL?.text?.isEmpty() == true){
                                Toast.makeText(this, "Missing Fields", Toast.LENGTH_SHORT).show()
                                foundEmpty = true
                                break
                            }
                        }
                    }
                }

                if (!foundEmpty){
                    addToDB()
                    addCheckToDB()
                    finish()
                }
            }
        }


        appViewModel.getWorkoutByWorkoutName(username, workoutName).observe(this, Observer {
            if(linearLayout.childCount == 0){
                for(i in it){
                    val exercise = i.exercise
                    val sets = i.sets
                    val reps = i.reps
                    val rest = i.rest
                    val weight = i.weight

                    val inflater = LayoutInflater.from(applicationContext)
                    val view= inflater.inflate(R.layout.exercise_item, null)
                    val deleteView: TextView = view.findViewById(R.id.right_view)


                    val exerciseView: EditText = view.findViewById(R.id.editTextTextPersonName)
                    exerciseView.setText(exercise)
                    exerciseView.isFocusable = false
                    exerciseView.setTextColor(Color.GRAY)
                    val setsView: EditText = view.findViewById(R.id.editTextNumber4)
                    setsView.setText(sets.toString())
                    val repsView: EditText = view.findViewById(R.id.editTextNumber5)
                    repsView.setText(reps.toString())
                    val restView: EditText = view.findViewById(R.id.editTextNumber6)
                    restView.setText(rest.toString())
                    val weightView: EditText = view.findViewById(R.id.editTextNumber2)
                    weightView.setText(weight.toString())

                    linearLayout.addView(view, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
                    deleteView.setOnClickListener { _ ->
                        appViewModel.updateCheckBoxStateChange(1, username, workoutName)
                        appViewModel.deleteWorkoutExercise(username, workoutName, exercise)
                        linearLayout.removeView(view)
                    }
                }
            }
        })
    }

    private fun addToDB(){
        var id = 0
        for ( i in 0 until linearLayout.childCount){
            val currExercise = ((linearLayout.getChildAt(i) as ConstraintLayout).getChildAt(0) as SwipeLayout).getChildAt(1) as LinearLayout
            val workout = Workout(workoutNameText.text.toString(), (currExercise.getChildAt(0) as EditText).text.toString(), (currExercise.getChildAt(1) as EditText).text.toString().toInt(), (currExercise.getChildAt(2) as EditText).text.toString().toInt(), (currExercise.getChildAt(3) as EditText).text.toString().toInt(), (currExercise.getChildAt(4) as EditText).text.toString().toInt(), username, 0L, id)
            id += 1
            appViewModel.insertWorkout(workout)
        }
    }

    private fun addCheckToDB(){
        var checkboxState: Array<IntArray> = arrayOf()
        var weightState: Array<IntArray> = arrayOf()
        var repsState: Array<IntArray> = arrayOf()
        for(i in 0 until linearLayout.childCount){
            val sets = ((((linearLayout.getChildAt(i) as ConstraintLayout).getChildAt(0) as SwipeLayout).getChildAt(1) as LinearLayout).getChildAt(1) as EditText).text.toString().toInt()
            val weight = ((((linearLayout.getChildAt(i) as ConstraintLayout).getChildAt(0) as SwipeLayout).getChildAt(1) as LinearLayout).getChildAt(4) as EditText).text.toString().toInt()
            val reps = ((((linearLayout.getChildAt(i) as ConstraintLayout).getChildAt(0) as SwipeLayout).getChildAt(1) as LinearLayout).getChildAt(2) as EditText).text.toString().toInt()
            checkboxState += IntArray(sets)
            weightState += IntArray(sets) { weight }
            repsState += IntArray(sets) { reps }
        }
        Log.println(Log.ERROR, "checkbox state", Gson().toJson(checkboxState))
        appViewModel.insertCheckBoxState(CheckboxState(username, workoutNameText.text.toString(), Gson().toJson(checkboxState), Gson().toJson(weightState), Gson().toJson(repsState), 0))
    }
}