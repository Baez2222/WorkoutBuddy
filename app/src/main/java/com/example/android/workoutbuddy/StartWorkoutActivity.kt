package com.example.android.workoutbuddy

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.*
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.example.android.workoutbuddy.databinding.ActivityStartworkoutBinding
import org.w3c.dom.Text
import java.io.IOException
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StartWorkoutActivity : AppCompatActivity(){

    private lateinit var binding: ActivityStartworkoutBinding
    private lateinit var textView_workoutName: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var button_finish: Button
    private var currentState: Parcelable? = null
    private var currentPersistent: Parcelable? = null
    private  lateinit var imageView_logo: ImageView

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

        // current workout
        val workoutName = intent.getStringExtra("workout")
        val username = intent.getStringExtra("username")
        // get list of exercises in that workout
        if (username != null && workoutName != null) {
            appViewModel.getWorkoutByWorkoutName(username, workoutName).observe(this, Observer {
                // recycler
                val adapter = WorkoutAdapter(it)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.onFlingListener = null
                recyclerView.setItemViewCacheSize(it.size)
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
                val currExercise = ((((recyclerView.getChildAt(0) as ConstraintLayout).getChildAt(2) as ScrollView).getChildAt(0) as CardView).getChildAt(0) as TableLayout)
                Log.println(Log.INFO, "child type", currExercise.javaClass.name)

                // for each row of the card
                for (j in 1 until currExercise.childCount){
                    var currRow = currExercise.getChildAt(j) as TableRow
                    var checkBox = currRow.getChildAt(3) as CheckBox
                    if (!checkBox.isEnabled && checkBox.isChecked){
                        // Exercise params
                        val workoutName = textView_workoutName.text.toString()
                        val exerciseName = ((recyclerView.getChildAt(0) as ConstraintLayout).getChildAt(0) as TextView).text.toString()
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

                        val exerciseInfo = Exercise(workoutName, exerciseName, reps, weight, date, username!!)
                        appViewModel.insertExercise(exerciseInfo)
                        Toast.makeText(this, workoutName + " " + exerciseName + " " + reps.toString() + " " + weight.toString() +" " + date +" " + username, Toast.LENGTH_SHORT).show()
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

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        currentState = recyclerView.layoutManager?.onSaveInstanceState()
        outState.putParcelable("key", currentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (!savedInstanceState.isEmpty){
            currentState = savedInstanceState.getParcelable("key")
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentState != null) {
            recyclerView.layoutManager?.onRestoreInstanceState(currentState)
        }
    }

//    override fun onStop() {
////        super.onStop()
////        bindService(Intent(this, VibrateService::class.java), )
////        val notificationIntent = Intent(this, VibrateService::class.java)
////        val pendingIntent = PendingIntent.getActivity(this, 0,
////                notificationIntent, 0)
////        val notification: Notification = NotificationCompat.Builder(this, "CHANNEL_ID")
////                .setSmallIcon(R.drawable.ic_launcher_background)
////                .setContentText("running in background")
////                .setContentIntent(pendingIntent).build()
//
////        startForegroundService(notificationIntent)
//    }

}