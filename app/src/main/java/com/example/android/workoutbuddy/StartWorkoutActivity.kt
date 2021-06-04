package com.example.android.workoutbuddy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.*
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.example.android.workoutbuddy.database.*
import com.example.android.workoutbuddy.databinding.ActivityStartworkoutBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class StartWorkoutActivity : AppCompatActivity(){

    private lateinit var binding: ActivityStartworkoutBinding
    private lateinit var textView_workoutName: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var button_finish: Button
    private lateinit var imageView_logo: ImageView

    private lateinit var username: String
    private lateinit var workoutName: String


    // vars for save instance
    private val LIST_STATE = "list_state"
    private var currentState: Parcelable? = null
    private val BUNDLE_RECYCLER_LAYOUT = "recycler_layout"
    private lateinit var workoutInstance : ArrayList<Workout>


    private val KEY_RECYCLER_STATE: String = "recycler_state"
    private var mBundleRecyclerViewState: Bundle? = null
    private var mListState: Parcelable? = null




    private val appViewModel : AppViewModel by viewModels{
        AppViewModelFactory((application as AppApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityStartworkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textView_workoutName = binding.textViewWorkoutName
        textView_workoutName.text = intent.getStringExtra("workout")
        recyclerView = binding.recyclerViewSWA
        button_finish = binding.buttonFinish
        imageView_logo = binding.imageView3

        // set view model
//        viewModel = ViewModelProvider(this).get(SavedStateViewModel::class.java)


        // current workout
        workoutName = intent.getStringExtra("workout").toString()
        username = intent.getStringExtra("username").toString()
        // get list of exercises in that workout

        appViewModel.getWorkoutByWorkoutName(username, workoutName).observe(this, Observer {
            // recycler
            val adapter = WorkoutAdapter(it, appViewModel)
            adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            recyclerView.adapter = adapter
            recyclerView.isSaveEnabled = true
            recyclerView.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.HORIZONTAL,
                    false
            )
            recyclerView.onFlingListener = null
            recyclerView.setItemViewCacheSize(it.size)
//                recyclerView.layoutManager?.onRestoreInstanceState(viewModel.getRVLayout())
            // snap to each item on scroll
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(recyclerView)
        })

        button_finish.setOnClickListener {
            for ( i in 0 until recyclerView.childCount){
                // current card
                val currExercise = ((((recyclerView.getChildAt(0) as ConstraintLayout).getChildAt(2) as ScrollView).getChildAt(
                        0
                ) as CardView).getChildAt(0) as TableLayout)
                Log.println(Log.INFO, "child type", currExercise.javaClass.name)

                // for each row of the card
                for (j in 1 until currExercise.childCount){
                    var currRow = currExercise.getChildAt(j) as TableRow
                    var checkBox = currRow.getChildAt(3) as CheckBox
                    if (!checkBox.isEnabled && checkBox.isChecked){
                        // Exercise params
                        val workoutName = textView_workoutName.text.toString()
                        val exerciseName = ((recyclerView.getChildAt(0) as ConstraintLayout).getChildAt(
                                0
                        ) as TextView).text.toString()
//                        val sets = currExercise.childCount - 1
                        var reps = 0
                        if ( (currRow.getChildAt(2) as EditText).text.isEmpty()){
                            reps = (currRow.getChildAt(2) as EditText).hint.toString().toInt()
                        }
                        else{
                            reps = (currRow.getChildAt(2) as EditText).text.toString().toInt()
                        }
                        var weight = 0
                        if ( (currRow.getChildAt(1) as EditText).text.isEmpty()){
                            weight = (currRow.getChildAt(1) as EditText).hint.toString().toInt()
                        }
                        else{
                            weight = (currRow.getChildAt(1) as EditText).text.toString().toInt()
                        }
                        val currentDate = LocalDate.now()
                        val date = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(currentDate)

                        val exerciseInfo = Exercise(
                                workoutName,
                                exerciseName,
                                reps,
                                weight,
                                date,
                                username
                        )
                        appViewModel.insertExercise(exerciseInfo)
                        Toast.makeText(
                                this,
                                workoutName + " " + exerciseName + " " + reps.toString() + " " + weight.toString() + " " + date + " " + username,
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            finish()
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





    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        // Save list state
        mListState = recyclerView.layoutManager?.onSaveInstanceState()
        state.putParcelable(KEY_RECYCLER_STATE, mListState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Retrieve list state and list/item positions
        mListState = savedInstanceState.getParcelable(KEY_RECYCLER_STATE)
    }

    override fun onStop() {
        super.onStop()
        mBundleRecyclerViewState = Bundle()
        mListState = recyclerView.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState?.putParcelable(KEY_RECYCLER_STATE, mListState)


        // update checkbox state
        val workoutName = textView_workoutName.text.toString()
        for ( i in 0 until recyclerView.childCount){
            // current card
            val currExercise = ((((recyclerView.getChildAt(0) as ConstraintLayout).getChildAt(2) as ScrollView).getChildAt(
                    0
            ) as CardView).getChildAt(0) as TableLayout)

            val exerciseName = ((recyclerView.getChildAt(0) as ConstraintLayout).getChildAt(
                    0
            ) as TextView).text.toString()

            // for each row of the card
            var checkBoxState = IntArray(currExercise.childCount - 1)
            for (j in 1 until currExercise.childCount){

                var currRow = currExercise.getChildAt(j) as TableRow
                var checkBox = currRow.getChildAt(3) as CheckBox
                if (!checkBox.isEnabled && checkBox.isChecked){
                    checkBoxState[j - 1] = 1
                }
                else if(checkBox.isEnabled && checkBox.isChecked){
                    checkBoxState[j - 1] = 2
                }
            }
            val gson = Gson()
            appViewModel.updateCheckBoxState(gson.toJson(checkBoxState), username, exerciseName, workoutName)
        }
    }


    override fun onPause() {
        super.onPause()

        mBundleRecyclerViewState = Bundle()
        mListState = recyclerView.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState?.putParcelable(KEY_RECYCLER_STATE, mListState)


        // update checkbox state
        val workoutName = textView_workoutName.text.toString()
        for ( i in 0 until recyclerView.childCount){
            // current card
            val currExercise = ((((recyclerView.getChildAt(0) as ConstraintLayout).getChildAt(2) as ScrollView).getChildAt(
                    0
            ) as CardView).getChildAt(0) as TableLayout)

            val exerciseName = ((recyclerView.getChildAt(0) as ConstraintLayout).getChildAt(
                    0
            ) as TextView).text.toString()

            // for each row of the card
            var checkBoxState = IntArray(currExercise.childCount - 1)
            for (j in 1 until currExercise.childCount){

                var currRow = currExercise.getChildAt(j) as TableRow
                var checkBox = currRow.getChildAt(3) as CheckBox
                if (!checkBox.isEnabled && checkBox.isChecked){
                    checkBoxState[j - 1] = 1
                }
                else if(checkBox.isEnabled && checkBox.isChecked){
                    checkBoxState[j - 1] = 2
                }
            }
            val gson = Gson()
            appViewModel.updateCheckBoxState(gson.toJson(checkBoxState), username, exerciseName, workoutName)
        }
    }


    override fun onRestart() {
        super.onRestart()

        if (mListState != null) {
            recyclerView.layoutManager?.onRestoreInstanceState(mListState)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mListState != null) {
            recyclerView.layoutManager?.onRestoreInstanceState(mListState)
        }
    }


}