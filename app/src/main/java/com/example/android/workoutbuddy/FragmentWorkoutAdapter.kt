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
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.cardview.widget.CardView
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android.workoutbuddy.database.Workout
import com.google.gson.Gson

class FragmentWorkoutAdapter(private val exercises: List<Workout>, private val activity: StartWorkoutMainActivity, private val username: String, private val workoutName: String, private var checkboxState: Array<IntArray>, private var weightState: Array<IntArray>, private var repsState: Array<IntArray>): RecyclerView.Adapter<FragmentWorkoutAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textView_exerciseName : TextView = view.findViewById(R.id.textView_SWAexerciseName)
        val tableLayout : TableLayout = view.findViewById(R.id.tableLayout_card)
        val cardView: CardView = view.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.exercise_card,
                parent,
                false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // replace fragment on cardView click
        holder.cardView.setOnClickListener {
            // create bundle with data to pass to fragment
            val bundle = Bundle()
            bundle.putString("username", username)
            bundle.putString("workoutName", workoutName)
            bundle.putString("exercise", exercises[position].exercise)
            bundle.putInt("sets", exercises[position].sets)
            bundle.putInt("reps", exercises[position].reps)
            bundle.putInt("weight", exercises[position].weight)
            bundle.putInt("rest", exercises[position].rest)
            bundle.putInt("position", position)
            bundle.putIntArray("checkboxState", checkboxState[position])
            bundle.putString("completeCheckboxState", Gson().toJson(checkboxState))
            bundle.putIntArray("weightState", weightState[position])
            bundle.putString("completeWeightState", Gson().toJson(weightState))
            bundle.putIntArray("repsState", repsState[position])
            bundle.putString("completeRepsState", Gson().toJson(repsState))

            val swaFragmentCard = FragmentCard()
            swaFragmentCard.arguments = bundle
            activity.loadFragment(swaFragmentCard, R.id.fragContainer, "CardFragment")
        }


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
            textView.setBackgroundColor(Color.BLACK)
            textView.setTextColor(Color.WHITE)
            tableRow.addView(textView)

            val edittextWeight = TextView(holder.tableLayout.context)
            edittextWeight.inputType = InputType.TYPE_CLASS_NUMBER
//            edittextWeight.hint = exercises[position].weight.toString()
            edittextWeight.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            )
            edittextWeight.gravity = Gravity.CENTER
            edittextWeight.setBackgroundColor(Color.BLACK)
            edittextWeight.setTextColor(Color.WHITE)
            edittextWeight.setHintTextColor(Color.LTGRAY)
            tableRow.addView(edittextWeight)
            if(weightState[position][i-1] != 0){
                edittextWeight.text = weightState[position][i-1].toString()
            }
            else{
                edittextWeight.hint = exercises[position].weight.toString()
            }

            val edittextReps = TextView(holder.tableLayout.context)
            edittextReps.inputType = InputType.TYPE_CLASS_NUMBER
//            edittextReps.hint = exercises[position].reps.toString()
            edittextReps.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            )
            edittextReps.gravity = Gravity.CENTER
            edittextReps.setBackgroundColor(Color.BLACK)
            edittextReps.setTextColor(Color.WHITE)
            edittextReps.setHintTextColor(Color.LTGRAY)
            tableRow.addView(edittextReps)
            if(repsState[position][i-1] != 0){
                edittextReps.text = repsState[position][i-1].toString()
            }
            else{
                edittextReps.hint = exercises[position].reps.toString()
            }

            val checkboxCompleted = AppCompatCheckBox(holder.tableLayout.context)
            checkboxCompleted.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER }
            CompoundButtonCompat.setButtonTintList(checkboxCompleted, ColorStateList.valueOf(Color.WHITE))
            checkboxCompleted.setBackgroundColor(Color.BLACK)
            checkboxCompleted.isEnabled = false

//            Log.println(Log.ERROR, "position:$position   + i:$i", checkboxState[position][i-1].toString())
            if(checkboxState[position][i-1] == 1){
                checkboxCompleted.isChecked = true
            }
            checkboxCompleted.isClickable = false

            tableRow.addView(checkboxCompleted)

            holder.tableLayout.addView(
                    tableRow,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }
}