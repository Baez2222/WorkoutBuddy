package com.example.android.workoutbuddy

import kotlinx.coroutines.flow.Flow

class AppRepository (private val appDAO: AppDAO) {

    // get all dreams
    val allUsers: Flow<List<User>> = appDAO.getAllUsers()

    val allWorkouts : Flow<List<Workout>> = appDAO.getAllWorkouts()

    val allExercises: Flow<List<Exercise>> = appDAO.getAllExercises()

    // suspend -> room runs all suspend functions/queries
    // so we just call it and embed in a method that we can use later

    suspend fun insert(user: User){
        appDAO.insert(user)
    }

    suspend fun insertWorkout(workout: Workout){
        appDAO.insertWorkout(workout)
    }

    suspend fun insertExercise(exercise: Exercise){
        appDAO.insertExercise(exercise)
    }

    suspend fun insertFood(food: Food){
        appDAO.insertFood(food)
    }

    suspend fun insertPicture(picture: Picture){
        appDAO.insertPicture(picture)
    }

//    suspend fun deleteById(id:Int){
//        dreamDao.delete(id)
//    }

    fun getUser(username:String): Flow<User> {
        return appDAO.getUser(username)
    }

    fun getWorkoutsByUsername(username: String): Flow<List<String>> {
        return appDAO.getWorkoutsByUsername(username)
    }

    fun getWorkoutByWorkoutName(username: String, workout: String): Flow<List<Workout>>{
        return appDAO.getWorkoutByWorkoutName(username, workout)
    }

    fun getWorkoutsByDate(username: String, date: String): Flow<List<Exercise>>{
        return appDAO.getWorkoutsByDate(username, date)
    }

    fun getExerciseSetCount(username: String, date: String, exercise: String): Flow<Int>{
        return appDAO.getExerciseSetCount(username, date, exercise)
    }
//    fun getWorkoutByDate(date:String) : Flow<Workout>{
//        return appDAO.getWorkoutByDate(date)
//    }

//    suspend fun update(id:Int, title:String, content:String, reflection:String, emotion:String){
//        return dreamDao.update(title, content, reflection, emotion, id)
//    }

    fun isUsernameTaken(username: String) : Flow<Int>{
        return appDAO.isUsernameTaken(username)
    }

    fun getCalorieIntake(username: String): Flow<Int>{
        return appDAO.getCalorieIntake(username)
    }
    suspend fun updateCalorieIntake(calorieIntake: Int, username: String){
        return appDAO.updateCalorieIntake(calorieIntake, username)
    }

    fun getFoodListByDate(date: String, username: String): Flow<List<Food>>{
        return appDAO.getFoodListByDate(date, username)
    }

    fun getPicture(date: String, username: String): Flow<Picture>{
        return appDAO.getPicture(date, username)
    }

}