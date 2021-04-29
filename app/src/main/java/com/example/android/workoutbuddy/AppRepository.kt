package com.example.android.workoutbuddy

import kotlinx.coroutines.flow.Flow

class AppRepository (private val appDAO: AppDAO) {

    // get all dreams
    val allUsers: Flow<List<User>> = appDAO.getAllUsers()

    val allWorkouts : Flow<List<Workout>> = appDAO.getAllWorkouts()

    // suspend -> room runs all suspend functions/queries
    // so we just call it and embed in a method that we can use later

    suspend fun insert(user: User){
        appDAO.insert(user)
    }

    suspend fun insertWorkout(workout: Workout){
        appDAO.insertWorkout(workout)
    }

//    suspend fun deleteById(id:Int){
//        dreamDao.delete(id)
//    }

    fun getUser(username:String): Flow<User> {
        return appDAO.getUser(username)
    }

    fun getWorkoutByDate(date:String) : Flow<Workout>{
        return appDAO.getWorkoutByDate(date)
    }

//    suspend fun update(id:Int, title:String, content:String, reflection:String, emotion:String){
//        return dreamDao.update(title, content, reflection, emotion, id)
//    }

    fun isUsernameTaken(username: String) : Flow<Int>{
        return appDAO.isUsernameTaken(username)
    }

}