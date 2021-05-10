package com.example.android.workoutbuddy

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class AppViewModel (private val repository: AppRepository) : ViewModel() {

    val allUsers: LiveData<List<User>> = repository.allUsers.asLiveData()

    val allWorkouts: LiveData<List<Workout>> = repository.allWorkouts.asLiveData()

    val allExercises : LiveData<List<Exercise>> = repository.allExercises.asLiveData()

    fun insert(user: User) = viewModelScope.launch{
        repository.insert(user)
    }

    fun insertWorkout(workout: Workout) = viewModelScope.launch {
        repository.insertWorkout(workout)
    }

    fun insertExercise(exercise: Exercise) = viewModelScope.launch {
        repository.insertExercise(exercise)
    }

    fun insertFood(food: Food) = viewModelScope.launch {
        repository.insertFood(food)
    }

    fun insertPicture(picture: Picture) = viewModelScope.launch {
        repository.insertPicture(picture)
    }

//    fun deleteById(id:Int) = viewModelScope.launch {
//        repository.deleteById(id)
//    }

    fun getUser(username:String) : LiveData<User> {
        return repository.getUser(username).asLiveData()
    }

    fun getWorkoutsByUsername(username: String): LiveData<List<String>>{
        return repository.getWorkoutsByUsername(username).asLiveData()
    }

    fun getWorkoutByWorkoutName(username: String, workout: String): LiveData<List<Workout>>{
        return repository.getWorkoutByWorkoutName(username, workout).asLiveData()
    }

    fun getWorkoutsByDate(username: String, date: String): LiveData<List<Exercise>>{
        return repository.getWorkoutsByDate(username, date).asLiveData()
    }

    fun getExerciseSetCount(username: String, date: String, exercise: String): LiveData<Int>{
        return repository.getExerciseSetCount(username, date, exercise).asLiveData()
    }

//    fun getWorkoutByName(date: String) : LiveData<Workout>{
//        return repository.getWorkoutByDate(date).asLiveData()
//    }

    fun isUsernameTaken(username: String) : LiveData<Int>{
        return repository.isUsernameTaken(username).asLiveData()
    }

    fun getCalorieIntake(username: String): LiveData<Int>{
        return repository.getCalorieIntake(username).asLiveData()
    }

    fun updateCalorieIntake(calorieIntake:Int, username: String) = viewModelScope.launch {
        repository.updateCalorieIntake(calorieIntake, username)
    }

    fun getFoodListByDate(date: String, username: String): LiveData<List<Food>>{
        return repository.getFoodListByDate(date, username).asLiveData()
    }

    fun getPicture(date: String, username: String): LiveData<Picture>{
        return repository.getPicture(date, username).asLiveData()
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