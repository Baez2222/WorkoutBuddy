package com.example.android.workoutbuddy

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.example.android.workoutbuddy.database.*
import com.example.android.workoutbuddy.databinding.ActivityStartworkoutmainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StartWorkoutMainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityStartworkoutmainBinding
    private lateinit var textView_workoutName: TextView
    private lateinit var fragContainer: FragmentContainerView
    private lateinit var button_finish: Button
    private lateinit var imageView_logo: ImageView

    private lateinit var username: String
    private lateinit var workoutName: String

    private lateinit var fragmentManager: FragmentManager

    private val appViewModel : AppViewModel by viewModels{
        AppViewModelFactory((application as AppApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityStartworkoutmainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textView_workoutName = binding.textViewWorkoutName
        textView_workoutName.text = intent.getStringExtra("workout")
        textView_workoutName.isAllCaps = true
        fragContainer = binding.fragContainer
        button_finish = binding.buttonFinish
        imageView_logo = binding.imageView3


        // current workout
        workoutName = intent.getStringExtra("workout").toString()
        username = intent.getStringExtra("username").toString()


        // create bundle with data to pass to fragment
        val bundle = Bundle()
        bundle.putString("username", username)
        bundle.putString("workoutName", workoutName)

        val swaRecyclerFragment = SWARecyclerFragment()
        swaRecyclerFragment.arguments = bundle


        loadFragment(swaRecyclerFragment, fragContainer.id, "RecyclerFragment")



        button_finish.setOnClickListener {
            if(!VibrateService.isRunning()){
                appViewModel.getCheckBoxState(username, workoutName).observe(this, Observer {
                    appViewModel.getWorkoutByWorkoutName(username, workoutName).observe(this, Observer { workoutList ->
                        val type: Type = object : TypeToken<Array<IntArray>>() {}.type
                        var checkboxState: Array<IntArray> = Gson().fromJson(it.checkboxState, type)
                        val weightState: Array<IntArray> = Gson().fromJson(it.weightState, type)
                        val repsState: Array<IntArray> = Gson().fromJson(it.repsState, type)

                        val date = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now())
                        for(i in checkboxState.indices){
                            for(j in checkboxState[i].indices){
                                appViewModel.insertExercise(Exercise(workoutName, workoutList[i].exercise, repsState[i][j], weightState[i][j], date, username))
                            }
                        }
                        resetCheckboxState()
                        finish()
                    })
                })
            }
        }

    }

    // based on the navigation component   (aka buttons)
    // I will decide which fragment to load into the placeholder
    fun loadFragment(fragment: Fragment, id: Int, tag: String) {
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
        if(tag == "CardFragment"){
            fragmentTransaction.addToBackStack(tag)
        }
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if(VibrateService.isRunning()){
            return
        }
        if(this::fragmentManager.isInitialized){
            if(fragmentManager.findFragmentByTag("RecyclerFragment")!!.isVisible){
                val popup = PopupWindow(this)
                val view = layoutInflater.inflate(R.layout.popupbackpressswa, null)
                popup.contentView = view
                popup.isOutsideTouchable = true

                val buttonYes = view.findViewById<Button>(R.id.button_backpressswaYes)
                val buttonDiscard = view.findViewById<Button>(R.id.button_backpressswaDiscard)

                buttonYes.setOnClickListener {
                    popup.dismiss()
                    super.onBackPressed()
                }

                buttonDiscard.setOnClickListener {
                    resetCheckboxState()
                    popup.dismiss()
                    super.onBackPressed()
                }
                popup.showAtLocation(view, Gravity.CENTER, 0, 0)
            }
            else{
                super.onBackPressed()
            }
        }

    }

    fun resetCheckboxState(){
        appViewModel.getCheckBoxState(username, workoutName).observe(this, Observer {
            val type: Type = object : TypeToken<Array<IntArray>>() {}.type
            var checkboxState: Array<IntArray> = Gson().fromJson(it.checkboxState, type)
            var weightState: Array<IntArray> = Gson().fromJson(it.weightState, type)
            var repsState: Array<IntArray> = Gson().fromJson(it.repsState, type)
            for(i in checkboxState.indices){
                for(j in checkboxState[i].indices){
                    checkboxState[i][j] = 0
                    weightState[i][j] = 0
                    repsState[i][j] = 0
                }
            }
            appViewModel.insertCheckBoxState(CheckboxState(username, workoutName, Gson().toJson(checkboxState), Gson().toJson(weightState), Gson().toJson(repsState)))
        })
    }


}