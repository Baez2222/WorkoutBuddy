package com.example.android.workoutbuddy.database

import androidx.lifecycle.*
import com.example.android.workoutbuddy.FirebaseUserLiveData
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class AppViewModel (private val repository: AppRepository) : ViewModel() {

    // user login authentication
    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

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

//    fun insertRecyclerInformation(recyclerInformation: RecyclerInformation) = viewModelScope.launch {
//        repository.insertRecyclerInformation(recyclerInformation)
//    }


//    fun deleteById(id:Int) = viewModelScope.launch {
//        repository.deleteById(id)
//    }
    fun deleteWorkout(username: String, workout: String) = viewModelScope.launch {
        repository.deleteWorkout(username, workout)
    }

    fun deletePicture(username: String, date: String) = viewModelScope.launch {
        repository.deletePicture(username, date)
    }

    fun deleteWorkoutExercise(username: String, workout: String, exercise: String) = viewModelScope.launch {
        repository.deleteWorkoutExercise(username, workout, exercise)
    }

    fun updateWorkoutExercise(sets: Int, reps: Int, rest: Int, weight: Int, username: String, workout: String, exercise: String) = viewModelScope.launch {
        repository.updateWorkoutExercise(sets, reps, rest, weight, username, workout, exercise)
    }
//    fun getRecyclerInformation(id:String): LiveData<RecyclerInformation>{
//        return repository.getRecyclerInformation(id).asLiveData()
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

//    fun updateCheckBoxState(checkboxState: String, username: String, exercise: String, workout: String) = viewModelScope.launch {
//        repository.updateCheckBoxState(checkboxState, username, exercise, workout)
//    }

    fun insertCheckBoxState(checkboxState: CheckboxState) = viewModelScope.launch {
        repository.insertCheckBoxState(checkboxState)
    }

    fun updateCheckBoxState(checkboxState: String, username: String, workout: String) = viewModelScope.launch {
        repository.updateCheckBoxState(checkboxState, username, workout)
    }

    fun updateCheckBoxStateWeight(weightState: String, username: String, workout: String) = viewModelScope.launch {
        repository.updateCheckBoxStateWeight(weightState, username, workout)
    }

    fun updateCheckBoxStateReps(repsState: String, username: String, workout: String) = viewModelScope.launch {
        repository.updateCheckBoxStateReps(repsState, username, workout)
    }

    fun updateCheckBoxStateChange(hasChanged: Int, username: String, workout: String) = viewModelScope.launch {
        repository.updateCheckBoxStateChange(hasChanged, username, workout)
    }

    fun getCheckBoxState(username: String, workout: String): LiveData<CheckboxState>{
        return repository.getCheckBoxState(username, workout).asLiveData()
    }

    fun updateTimeLeft(timeLeft: Long, username: String, exercise: String, workout: String) = viewModelScope.launch {
        repository.updateTimeLeft(timeLeft, username, exercise, workout)
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


    val allCheckbox: LiveData<List<Checkbox>> = repository.allCheckbox.asLiveData()

    fun insertCheckbox(checkbox: Checkbox) = viewModelScope.launch {
        repository.insertCheckbox(checkbox)
    }

    fun updateCheckbox(currCheckbox: Int, timerHolderPosition: Int, tag:String) = viewModelScope.launch {
        repository.updateCheckbox(currCheckbox, timerHolderPosition, tag)
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