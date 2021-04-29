package com.example.android.workoutbuddy

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class AppViewModel (private val repository: AppRepository) : ViewModel() {

    val allUsers: LiveData<List<User>> = repository.allUsers.asLiveData()

    val allWorkouts: LiveData<List<Workout>> = repository.allWorkouts.asLiveData()

    fun insert(user: User) = viewModelScope.launch{
        repository.insert(user)
    }

    fun insertWorkout(workout: Workout) = viewModelScope.launch {
        repository.insertWorkout(workout)
    }

//    fun deleteById(id:Int) = viewModelScope.launch {
//        repository.deleteById(id)
//    }

    fun getUser(username:String) : LiveData<User> {
        return repository.getUser(username).asLiveData()
    }

    fun getWorkoutByName(date: String) : LiveData<Workout>{
        return repository.getWorkoutByDate(date).asLiveData()
    }

    fun isUsernameTaken(username: String) : LiveData<Int>{
        return repository.isUsernameTaken(username).asLiveData()
    }

//    fun update(id:Int, title:String, content:String, reflection:String, emotion:String) = viewModelScope.launch {
//        repository.update(id, title, content, reflection, emotion)
//    }
}

class AppViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)){
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}