package com.example.android.workoutbuddy

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.android.workoutbuddy.database.AppApplication
import com.example.android.workoutbuddy.database.AppViewModel
import com.example.android.workoutbuddy.database.AppViewModelFactory
import com.example.android.workoutbuddy.database.Workout
import com.example.android.workoutbuddy.databinding.ActivityCreateworkoutBinding
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream

class CreateWorkoutActivity: AppCompatActivity() {

    private lateinit var binding : ActivityCreateworkoutBinding
    private lateinit var editText_workoutName: EditText
    private lateinit var button_add: Button
    private lateinit var button_submit: Button
    private lateinit var linearLayout: LinearLayout
    private lateinit var username : String
    private lateinit var imageView_logo: ImageView

    private val appViewModel : AppViewModel by viewModels{
        AppViewModelFactory((application as AppApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateworkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editText_workoutName = binding.editTextWorkoutName
        button_add = binding.buttonAddExercise
        button_submit = binding.buttonSubmit
        linearLayout = binding.linearLayoutExercises
        imageView_logo = binding.imageView2

        username = intent.getStringExtra("username").toString()

        button_add.setOnClickListener {
            val inflater = LayoutInflater.from(applicationContext)
            val view= inflater.inflate(R.layout.exercise_item, null)
//            val view = LayoutInflater.from(this).inflate(R.layout.exercise_item, null)
            linearLayout.addView(view, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        }

        button_submit.setOnClickListener {
            Log.println(Log.DEBUG, "children", linearLayout.childCount.toString())
            if (linearLayout.childCount == 0){
                Toast.makeText(this, "Exercise missing", Toast.LENGTH_SHORT).show()
            }
            else if (TextUtils.isEmpty(editText_workoutName.text)){
                Toast.makeText(this, "Workout name missing", Toast.LENGTH_SHORT).show()
            }
            else{
                var foundEmpty = false
                for ( i in 0..linearLayout.childCount){
                    if (foundEmpty){ break }
                    val childLayout = linearLayout.getChildAt(i)
                    if ( childLayout is ConstraintLayout){
                        val currLL = childLayout.getChildAt(0) as LinearLayout
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
                    finish()
                }
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

    private fun addToDB(){
        for ( i in 0..linearLayout.childCount){
            val currExercise = (linearLayout.getChildAt(i) as ConstraintLayout).getChildAt(0) as LinearLayout
            //Log.println(Log.ERROR, "composite key: ", editText_workoutName.toString() + " , " + username)
            val gson = Gson()
            val checkboxState = gson.toJson(IntArray((currExercise.getChildAt(2) as EditText).text.toString().toInt()))
            Log.println(Log.DEBUG, "checkboxState", checkboxState)
            val workout = Workout(editText_workoutName.text.toString(), (currExercise.getChildAt(0) as EditText).text.toString(), (currExercise.getChildAt(1) as EditText).text.toString().toInt(), (currExercise.getChildAt(2) as EditText).text.toString().toInt(), (currExercise.getChildAt(3) as EditText).text.toString().toInt(), (currExercise.getChildAt(4) as EditText).text.toString().toInt(), username, checkboxState, 0L)
            appViewModel.insertWorkout(workout)
        }
    }

}