package com.example.android.workoutbuddy

import android.os.CountDownTimer
import android.text.InputType
import android.util.Log
import android.util.SparseBooleanArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.android.workoutbuddy.database.Workout
import java.util.concurrent.TimeUnit


class WorkoutAdapter(private val exercises: List<Workout>) : RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {

    private lateinit var timer: CountDownTimer
    private var restoreHolder : WorkoutAdapter.ViewHolder? = null
    private var pos = -1

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textView_exerciseName : TextView = view.findViewById(R.id.textView_SWAexerciseName)
        val tableLayout : TableLayout = view.findViewById(R.id.tableLayout_card)
        val time : TextView = view.findViewById(R.id.time)
//        val wakeLock : PowerManager.WakeLock = (view.context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
//            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
//                acquire()
//            }
//        }


        var itemCheckBoxState = SparseBooleanArray()

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








        if(position != RecyclerView.NO_POSITION){
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
                checkBox_completed.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT).apply { gravity = Gravity.CENTER }

                // check if checked or not
                if(!holder.itemCheckBoxState.get((position.toString() + "00" + i.toString()).toInt(), false)){
                    checkBox_completed.isChecked = false
                    val key = (position.toString() + "00" + i.toString()).toInt()
                    holder.itemCheckBoxState.put(key, false)
                }
                else{
                    checkBox_completed.isChecked = true
                    val key = (position.toString() + "00" + i.toString()).toInt()
                    holder.itemCheckBoxState.put(key, true)
                }


                checkBox_completed.setOnCheckedChangeListener { buttonView, isChecked ->

                    Log.println(Log.INFO, "!!!!", "am inside click listener")
                    if (checkBox_completed.isChecked  && holder.time.text != ""){
                        checkBox_completed.isChecked = false
                        val key = (position.toString() + "00" + i.toString()).toInt()
                        holder.itemCheckBoxState.put(key, false)
                    }
                    else if ( checkBox_completed.isChecked && holder.time.text == ""){
                        val key = (position.toString() + "00" + i.toString()).toInt()
                        holder.itemCheckBoxState.put(key, true)
                        VibrateService.startService(holder.tableLayout.context, "Vibrate Foreground Service is running...") // start service
                        timer = object: CountDownTimer(exercises[position].rest.toLong() * 1000, 1000){
                            override fun onTick(p0: Long) {
                                val minutes = TimeUnit.MILLISECONDS.toMinutes(p0)
                                var seconds = TimeUnit.MILLISECONDS.toSeconds(p0)%60
                                if (seconds < 10){
                                    holder.time.text = "00:0" + seconds.toString()
                                }
                                else{
                                    holder.time.text = "$minutes:$seconds"
                                }
                            }

                            override fun onFinish() {
                                VibrateService.stopService(holder.tableLayout.context, true) // end service
//                            notificationManager.notify(100, builder.build())
//                            val vibratorService = holder.tableLayout.context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//                            vibratorService.vibrate(VibrationEffect.createOneShot(2000, 150))
                                holder.time.text = ""
                                checkBox_completed.isEnabled = false
//                            holder.wakeLock.acquire()
                            }

                        }
                        timer.start()
                    }
                    else{
                        VibrateService.stopService(holder.tableLayout.context, false)
                        timer.cancel()
                        holder.time.text = ""
                        val key = (position.toString() + "00" + i.toString()).toInt()
                        holder.itemCheckBoxState.put(key, false)
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





        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

}