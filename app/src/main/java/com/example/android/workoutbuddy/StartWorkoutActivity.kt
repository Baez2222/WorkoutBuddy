package com.example.android.workoutbuddy

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
import java.io.IOException
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class StartWorkoutActivity : AppCompatActivity(){

    private lateinit var binding: ActivityStartworkoutBinding
    private lateinit var textView_workoutName: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var button_finish: Button
    private  lateinit var imageView_logo: ImageView


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
        val workoutName = intent.getStringExtra("workout")
        val username = intent.getStringExtra("username")
        // get list of exercises in that workout
        if (username != null && workoutName != null) {
            appViewModel.getWorkoutByWorkoutName(username, workoutName).observe(this, Observer {
                // recycler
                val adapter = WorkoutAdapter(it)
                recyclerView.adapter = adapter
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

//                val fragmentManager = supportFragmentManager
//                // create a fragment transaction to begin the transaction and replace the fragment
//                // create a fragment transaction to begin the transaction and replace the fragment
//                val fragmentTransaction = fragmentManager.beginTransaction()
//                //replacing the placeholder - fragmentContainterView with the fragment that is passed as parameter
//                //replacing the placeholder - fragmentContainterView with the fragment that is passed as parameter
//                fragmentTransaction.replace(R.id.fragContainer, WorkoutFragment())
//                fragmentTransaction.commit()


            })
        }

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
                                username!!
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


//    override fun onPause() {
//        super.onPause()
//        mBundleRecyclerViewState = Bundle()
//        mListState = recyclerView.layoutManager?.onSaveInstanceState()
//        mBundleRecyclerViewState?.putParcelable(KEY_RECYCLER_STATE, mListState)
//    }

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
    }

//    override fun onResume() {
//        super.onResume()
//            mListState = mBundleRecyclerViewState?.getParcelable(KEY_RECYCLER_STATE)
//            recyclerView.layoutManager?.onRestoreInstanceState(mListState)
//    }
    override fun onResume() {
        super.onResume()
        if (mListState != null) {
            recyclerView.layoutManager?.onRestoreInstanceState(mListState)
        }
}

    override fun onRestart() {
        super.onRestart()

        if (mListState != null) {
            recyclerView.layoutManager?.onRestoreInstanceState(mListState)
        }
//        mListState = mBundleRecyclerViewState!!.getParcelable(KEY_RECYCLER_STATE)
//        recyclerView.layoutManager?.onRestoreInstanceState(mListState)
//        Handler(Looper.myLooper()!!).postDelayed({
//        }, 50)
    }



//    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
//        super.onSaveInstanceState(outState, outPersistentState)
//        outState.putParcelableArrayList(LIST_STATE, workoutInstance)
//        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.layoutManager?.onSaveInstanceState())
//
////        currentState = recyclerView.layoutManager?.onSaveInstanceState()
////        outState.putParcelable("key", currentState)
////        outState.putString("workoutName", textView_workoutName.text.toString())
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        if (!savedInstanceState.isEmpty){
//            currentState = savedInstanceState.getParcelable("key")
//            textView_workoutName.text = savedInstanceState.getString("workoutName")
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        recyclerView.layoutManager?.onRestoreInstanceState(currentState!!)
//
////        recyclerView.layoutManager?.onRestoreInstanceState(appViewModel.getRecyclerInformation("RVLM").value?.layout)
//    }


}