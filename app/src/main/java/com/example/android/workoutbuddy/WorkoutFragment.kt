package com.example.android.workoutbuddy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class WorkoutFragment: Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_workout, container, false)
        val linearLayout : LinearLayout = view.findViewById(R.id.linearLayout_frag)

        val view_inside = LayoutInflater.from(view.context).inflate(R.layout.exercise_card, container, false)
        linearLayout.addView(view_inside, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        return view
    }
}