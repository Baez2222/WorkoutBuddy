package com.example.android.workoutbuddy

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.CompoundButtonCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.workoutbuddy.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.krtkush.lineartimer.LinearTimer
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit


class FragmentCard: Fragment(), SimpleCountDownTimer.OnCountDownListener {

    private lateinit var view_: View
    private lateinit var username: String
    private lateinit var workoutName: String
    private lateinit var exercise: String
    private var sets: Int = -1
    private var reps: Int = -1
    private var weight: Int = -1
    private var rest: Int = -1
    private var position: Int = -1

    private lateinit var textView_exerciseName: TextView
    private lateinit var tableLayout: TableLayout
    private lateinit var textView_Timer: TextView
    lateinit var checkboxState: Array<IntArray>
    lateinit var weightState: Array<IntArray>
    lateinit var repsState: Array<IntArray>
    lateinit var currentCheckboxState: IntArray
    private var currentCheckbox: Int = -1
    private lateinit var simpleCountDownTimer: SimpleCountDownTimer
    private lateinit var linearTimer: LinearTimer

    private lateinit var mContext: Context

    private val appViewModel : AppViewModel by viewModels{
        AppViewModelFactory((activity?.application as AppApplication).repository)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        appViewModel.allCheckbox.observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                currentCheckbox = it[0].currCheckbox
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view_ = inflater.inflate(R.layout.fragment_swacard, container, false)


        username = requireArguments().getString("username").toString()
        workoutName = requireArguments().getString("workoutName").toString()
        exercise = requireArguments().getString("exercise").toString()
        sets = requireArguments().getInt("sets")
        reps = requireArguments().getInt("reps")
        weight = requireArguments().getInt("weight")
        rest = requireArguments().getInt("rest")
        position = requireArguments().getInt("position")
        currentCheckboxState = requireArguments().getIntArray("checkboxState")!!
        val type: Type = object : TypeToken<Array<IntArray>>() {}.type
        checkboxState = Gson().fromJson(requireArguments().getString("completeCheckboxState"), type)
        weightState = Gson().fromJson(requireArguments().getString("completeWeightState"), type)
        repsState = Gson().fromJson(requireArguments().getString("completeRepsState"), type)

        textView_exerciseName = view_.findViewById(R.id.textView_exerciseFragCard)
        tableLayout = view_.findViewById(R.id.tableLayout_fragCard)
        textView_Timer = view_.findViewById(R.id.textView_timerCard)



        for(i in 1..sets){
            textView_exerciseName.text = exercise
            val tableRow = TableRow(mContext)
            tableRow.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            )

            val textView = TextView(mContext)
            textView.text = (i).toString()
            textView.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            )
            textView.gravity = Gravity.CENTER
            textView.setBackgroundColor(Color.BLACK)
            textView.setTextColor(Color.WHITE)
            tableRow.addView(textView)

            val edittextWeight = EditText(mContext)
            edittextWeight.inputType = InputType.TYPE_CLASS_NUMBER
            edittextWeight.hint = weight.toString()
            edittextWeight.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            )
            edittextWeight.gravity = Gravity.CENTER
            edittextWeight.setBackgroundColor(Color.BLACK)
            edittextWeight.setTextColor(Color.WHITE)
            edittextWeight.setHintTextColor(Color.LTGRAY)
            tableRow.addView(edittextWeight)
            edittextWeight.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    //SAVE THE DATA
                    if(edittextWeight.text.toString() == ""){
                        weightState[position][i-1] = edittextWeight.hint.toString().toInt()
                    }
                    else{
                        weightState[position][i-1] = edittextWeight.text.toString().toInt()
                    }
                    appViewModel.updateCheckBoxStateWeight(Gson().toJson(weightState), username, workoutName)
                }
            }

            val edittextReps = EditText(mContext)
            edittextReps.inputType = InputType.TYPE_CLASS_NUMBER
            edittextReps.hint = reps.toString()
            edittextReps.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            )
            edittextReps.gravity = Gravity.CENTER
            edittextReps.setBackgroundColor(Color.BLACK)
            edittextReps.setTextColor(Color.WHITE)
            edittextReps.setHintTextColor(Color.LTGRAY)
            tableRow.addView(edittextReps)
            edittextReps.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    //SAVE THE DATA
                    if(edittextReps.text.toString() == ""){
                        repsState[position][i-1] = edittextReps.hint.toString().toInt()
                    }
                    else{
                        repsState[position][i-1] = edittextReps.text.toString().toInt()
                    }
                    appViewModel.updateCheckBoxStateReps(Gson().toJson(repsState), username, workoutName)
                }
            }

            val checkboxCompleted = CheckBox(mContext)
            checkboxCompleted.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER }
            CompoundButtonCompat.setButtonTintList(checkboxCompleted, ColorStateList.valueOf(Color.WHITE))
            checkboxCompleted.setBackgroundColor(Color.BLACK)

            // check if set has already been completed
//                Log.println(Log.ERROR, "checkboxstate", checkboxState[i - 1].toString())

            when {
                currentCheckboxState[i - 1] == 0 -> { checkboxCompleted.isChecked = false }
                currentCheckboxState[i - 1] == 1 -> { checkboxCompleted.isChecked = true; checkboxCompleted.isEnabled = true }
                currentCheckboxState[i - 1] == 2 -> { checkboxCompleted.isChecked = true; checkboxCompleted.isEnabled = false }
            }


            checkboxCompleted.setOnCheckedChangeListener { buttonView, isChecked ->

                val minutes = TimeUnit.MILLISECONDS.toMinutes(rest.toLong() * 1000)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(rest.toLong() * 1000)%60

                // prevents another checkbox from checked while a timer is running
                if (isChecked  && textView_Timer.text != ""){
                    buttonView.isChecked = false
                }
                // cancels timer if current checkbox had it running
                else if(!isChecked && textView_Timer.text != "" && currentCheckbox == i-1){
                    buttonView.isChecked = true
                    VibrateService.stopService(requireContext(), false)
                    simpleCountDownTimer.cancel()
                    linearTimer.restartTimer()
                    linearTimer.pauseTimer()
                    currentCheckbox = -1
                    checkboxCompleted.text = ""
                    appViewModel.insertCheckbox(Checkbox(currentCheckbox, -1, "CardFragment", exercise, workoutName, 1))
                }
                // start rest timer on check
                else if ( (isChecked && textView_Timer.text == "" && currentCheckbox == -1) || (isChecked && textView_Timer.text == "" && currentCheckbox != -1 && !VibrateService.isRunning()) ){
                    currentCheckboxState[i - 1] = 1
                    currentCheckbox = i-1

                    VibrateService.startService(requireContext(), "Vibrate Foreground Service is running...") // start service
                    simpleCountDownTimer = SimpleCountDownTimer(minutes, seconds, this)
                    simpleCountDownTimer.start(false)
                    linearTimer = LinearTimer.Builder().linearTimerView(view_.findViewById(R.id.linearTimer)).duration((rest.toLong() + 2) * 1000L).build()
                    linearTimer.startTimer()
                    checkboxState[position] = currentCheckboxState
                    appViewModel.updateCheckBoxState(Gson().toJson(checkboxState), username, workoutName)
                    appViewModel.insertCheckbox(Checkbox(currentCheckbox, -1, "CardFragment", exercise, workoutName, 1))
                }
                // unchecks checkbox that has no running timer
                else{
                    if(!VibrateService.isRunning()){
                        currentCheckbox = -1
                    }
                    Log.println(Log.ERROR, "inside else", currentCheckbox.toString())
                    currentCheckboxState[i - 1] = 0
                    checkboxState[position] = currentCheckboxState
                    appViewModel.updateCheckBoxState(Gson().toJson(checkboxState), username, workoutName)
                    appViewModel.insertCheckbox(Checkbox(currentCheckbox, -1, "CardFragment", exercise, workoutName, 1))
                }
//                Log.println(Log.ERROR, "currCheckbox_onCheckChangeListener_end", currCheckbox.toString())
            }




            tableRow.addView(checkboxCompleted)

            tableLayout.addView(
                    tableRow,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT
            )
        }

        return view_
    }

    override fun onCountDownActive(time: String) {
        activity?.runOnUiThread {
            Log.println(Log.ERROR, "on countdown", "reached runonuithread")
            textView_Timer.text = time
        }
    }

    override fun onCountDownFinished(cancel: Boolean) {
        activity?.runOnUiThread {
            textView_Timer.text = ""
        }
        if(!cancel){
            VibrateService.stopService(requireContext(), true)
            currentCheckbox = -1
        }
    }

//    override fun onStop() {
//        super.onStop()
//        appViewModel.updateCheckBoxState(Gson().toJson(checkboxState), username, workoutName)
//        appViewModel.insertCheckbox(Checkbox(currentCheckbox, -1, "CardFragment", exercise, workoutName, 1))
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        appViewModel.getCheckBoxState(username, workoutName).observe(this.activity as LifecycleOwner, Observer {
//            val type: Type = object : TypeToken<IntArray>() {}.type
//            checkboxState = if(it != null && it.checkboxState != "" && it.checkboxState != "[]"){
//                Gson().fromJson(it.checkboxState, type)
//            } else{
//                IntArray(sets)
//            }
//        })
//    }
}