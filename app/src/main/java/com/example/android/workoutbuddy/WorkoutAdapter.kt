package com.example.android.workoutbuddy

import android.app.Activity
import android.graphics.Color
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.android.workoutbuddy.database.AppViewModel
import com.example.android.workoutbuddy.database.Workout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


class WorkoutAdapter(private val exercises: List<Workout>, private val appViewModel: AppViewModel, private val activity: Activity, private val textView_Timer: TextView, private var currCheckbox: Int, private val username: String, private val workout: String, private var currentCheckboxStates: Array<IntArray>) : RecyclerView.Adapter<WorkoutAdapter.ViewHolder>(), SimpleCountDownTimer.OnCountDownListener {

    private lateinit var timer: CountDownTimer
    private var pos = -1
    private var currId : Int = 10000
    private lateinit var simpleCountDownTimer: SimpleCountDownTimer
    private lateinit var checkboxState: IntArray
    private lateinit var currentCheckboxToDisable: CheckBox
    private lateinit var view_: View



    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textView_exerciseName : TextView = view.findViewById(R.id.textView_SWAexerciseName)
        val tableLayout : TableLayout = view.findViewById(R.id.tableLayout_card)
//        val wakeLock : PowerManager.WakeLock = (view.context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
//            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
//                acquire()
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.exercise_card,
                parent,
                false
        )

        val holder = ViewHolder(view)

//        val position = holder.adapterPosition
        pos = pos + 1
        val position = pos


//        val gson = Gson()
//        val type: Type = object : TypeToken<Array<IntArray>>() {}.type
//        checkboxState = gson.fromJson(
//                currentCheckboxStates[position],
//                type
//        )



        if(position != RecyclerView.NO_POSITION){
            checkboxState = currentCheckboxStates[position]
            for(i in 1..exercises[position].sets){
                holder.textView_exerciseName.text = exercises[position].exercise
                val tableRow = TableRow(holder.tableLayout.context)
                tableRow.layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                )

                val textView = TextView(holder.tableLayout.context)
                textView.text = (i).toString()
                textView.layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                )
                textView.gravity = Gravity.CENTER
                tableRow.addView(textView)

                val editText_weight = EditText(holder.tableLayout.context)
                editText_weight.inputType = InputType.TYPE_CLASS_NUMBER
                editText_weight.hint = exercises[position].weight.toString()
                editText_weight.layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                )
                editText_weight.gravity = Gravity.CENTER
                tableRow.addView(editText_weight)

                val editText_reps = EditText(holder.tableLayout.context)
                editText_reps.inputType = InputType.TYPE_CLASS_NUMBER
                editText_reps.hint = exercises[position].reps.toString()
                editText_reps.layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                )
                editText_reps.gravity = Gravity.CENTER
                tableRow.addView(editText_reps)

                val checkBox_completed = CheckBox(holder.tableLayout.context)
                checkBox_completed.id = currId
                currId += 1
                checkBox_completed.layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                ).apply { gravity = Gravity.CENTER }

                // check if set has already been completed
//                Log.println(Log.ERROR, "checkboxstate", checkboxState[i - 1].toString())

                when {
                    checkboxState[i-1] == 0 -> { checkBox_completed.isChecked = false }
                    checkboxState[i-1] == 1 -> { checkBox_completed.isChecked = true; checkBox_completed.isEnabled = true }
                    checkboxState[i-1] == 2 -> { checkBox_completed.isChecked = true; checkBox_completed.isEnabled = false }
                }




                checkBox_completed.setOnCheckedChangeListener { buttonView, isChecked ->

                    val minutes = TimeUnit.MILLISECONDS.toMinutes(exercises[position].rest.toLong() * 1000)
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(exercises[position].rest.toLong() * 1000)%60
                    Log.println(Log.INFO, "!!!!", "inside click listener")
                    Log.println(Log.ERROR, "currCheckbox", currCheckbox.toString())

                    // prevents another checkbox from checked while a timer is running
                    if (checkBox_completed.isChecked  && textView_Timer.text != "" && currCheckbox != -1){
                        checkBox_completed.isChecked = false
                    }
                    // cancels timer if current checkbox had it running
                    else if(!checkBox_completed.isChecked && textView_Timer.text != ""){
                        if(currCheckbox == i-1){
                            checkBox_completed.isChecked = true
                            VibrateService.stopService(holder.tableLayout.context, false)
                            simpleCountDownTimer.cancel()
                            currCheckbox = -1
//                            appViewModel.updateCheckBoxState(Gson().toJson(checkboxState), username, holder.textView_exerciseName.text.toString(), workout)
//                            Log.println(Log.ERROR, "currCheckbox_1", currCheckbox.toString())
                        }
                    }
                    // start rest timer on check
                    else if ( checkBox_completed.isChecked && textView_Timer.text == "" && currCheckbox == -1){
                        checkboxState[i-1] = 1

                        simpleCountDownTimer = SimpleCountDownTimer(minutes, seconds, this)
                        VibrateService.startService(holder.tableLayout.context, "Vibrate Foreground Service is running...") // start service

                        currCheckbox = i-1
                        simpleCountDownTimer.start(false, currCheckbox, holder.textView_exerciseName.text.toString(), position)
                        simpleCountDownTimer.runOnBackgroundThread()
                        Log.println(Log.ERROR, "currCheckbox_2", currCheckbox.toString())
//                        appViewModel.updateCheckBoxState(Gson().toJson(checkboxState), username, holder.textView_exerciseName.text.toString(), workout)

                        currentCheckboxStates[position] = checkboxState
                    }
                    // unchecks checkbox that has no running timer
                    else{
//                        VibrateService.stopService(holder.tableLayout.context, false)
//                        timer.cancel()
//                        textView_Timer.text = ""
                        checkboxState[i - 1] = 0
                        currCheckbox = -1
//                        appViewModel.updateCheckBoxState(Gson().toJson(checkboxState), username, holder.textView_exerciseName.text.toString(), workout)
//                        Log.println(Log.ERROR, "layoutPosition", holder.layoutPosition.toString())

                        currentCheckboxStates[position] = checkboxState
                    }

                }
//            checkBox_completed.setOnCheckedChangeListener{ buttonView, isChecked -> mOnChecked?.onCheck(1)}

                tableRow.addView(checkBox_completed)

                holder.tableLayout.addView(
                        tableRow,
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT
                )
            }

        }
        view_ = view

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onCountDownActive(time: String) {
//        globalTime.post { globalTime.text = time }

        activity.runOnUiThread {
            textView_Timer.text = time
        }
    }

    override fun onCountDownFinished(cancel: Boolean) {
        activity.runOnUiThread {
            textView_Timer.text = ""
        }
        if(!cancel){
            VibrateService.stopService(textView_Timer.context, true)
            checkboxState[simpleCountDownTimer.getCurrCheckbox()] = 1
            currentCheckboxStates[simpleCountDownTimer.getHolderPosition()] = checkboxState
//            appViewModel.updateCheckBoxState(Gson().toJson(checkboxState), username, workout)
        }
        else{
            VibrateService.stopService(textView_Timer.context, false)
            checkboxState[simpleCountDownTimer.getCurrCheckbox()] = 1
            currentCheckboxStates[simpleCountDownTimer.getHolderPosition()] = checkboxState
//            appViewModel.updateCheckBoxState(Gson().toJson(checkboxState), username, workout)
//            VibrateService.stopService(textView_Timer.context, false)
//            checkboxState[currCheckbox] = 0
//            currCheckbox = -1
        }
    }

    override fun updateViewModel() {
//        appViewModel.updateCheckBoxState(Gson().toJson(checkboxState), username, workout)
    }

}