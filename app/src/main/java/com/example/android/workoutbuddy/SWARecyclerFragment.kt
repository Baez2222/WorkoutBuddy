package com.example.android.workoutbuddy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.android.workoutbuddy.database.AppApplication
import com.example.android.workoutbuddy.database.AppViewModel
import com.example.android.workoutbuddy.database.AppViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SWARecyclerFragment: Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var view_: View
    private lateinit var username: String
    private lateinit var workoutName: String

    private val appViewModel : AppViewModel by viewModels{
        AppViewModelFactory((activity?.application as AppApplication).repository)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        view_ = inflater.inflate(R.layout.fragment_swarecycler, container, false)

        recyclerView = view_.findViewById(R.id.recyclerView_SWAfrag)

        username = requireArguments().getString("username").toString()
        workoutName = requireArguments().getString("workoutName").toString()

        appViewModel.getWorkoutByWorkoutName(username, workoutName).observe(this.activity as LifecycleOwner, androidx.lifecycle.Observer {
            appViewModel.getCheckBoxState(username, workoutName).observe(this.activity as LifecycleOwner, Observer { checkboxJSON ->
                var checkboxState: Array<IntArray> = arrayOf()
                var weightState: Array<IntArray> = arrayOf()
                var repsState: Array<IntArray> = arrayOf()
                if(checkboxJSON.hasChanged == 0){
                    val type: Type = object : TypeToken<Array<IntArray>>() {}.type
                    checkboxState = Gson().fromJson(checkboxJSON.checkboxState, type)
                    weightState = Gson().fromJson(checkboxJSON.weightState, type)
                    repsState = Gson().fromJson(checkboxJSON.repsState, type)
                }
                else{
                    for(i in it){
                        checkboxState += IntArray(i.sets)
                        weightState += IntArray(i.sets) { i.weight }
                        repsState += IntArray(i.sets) { i.reps }
                    }
                    appViewModel.updateCheckBoxStateChange(0, username, workoutName)
                }


                val adapter = FragmentWorkoutAdapter(it, (activity as StartWorkoutMainActivity), username, workoutName, checkboxState, weightState, repsState)
                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                recyclerView.isSaveEnabled = true
                recyclerView.layoutManager = LinearLayoutManager(
                        this.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
//            if(timerHolderPosition != -1){
//                recyclerView.scrollToPosition(timerHolderPosition)
//            }
                recyclerView.adapter = adapter
                recyclerView.onFlingListener = null
                recyclerView.setItemViewCacheSize(it.size)
//                recyclerView.layoutManager?.onRestoreInstanceState(viewModel.getRVLayout())
                // snap to each item on scroll
                val snapHelper = PagerSnapHelper()
                snapHelper.attachToRecyclerView(recyclerView)
            })
        })

        return view_
    }
}