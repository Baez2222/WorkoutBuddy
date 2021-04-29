package com.example.android.workoutbuddy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDAO {

    //Get all dreams; sort the results by id
    @Query("SELECT * FROM user_table")
    fun getAllUsers() : Flow<List<User>>

    @Query("SELECT * FROM workout_table")
    fun getAllWorkouts() : Flow<List<Workout>>

    //Insert a dream; if there is conflict, we ignore the insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user:User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkout(workout: Workout)

    //Update a dream with a given id and a set of fields
//    @Query("UPDATE dream_table SET title=:title, content=:content, reflection=:reflection, emotion=:emotion WHERE id=:id")
//    suspend fun update(title:String, content:String, reflection:String, emotion:String, id:Int)

    //Delete a dream with a given id
//    @Query("DELETE FROM dream_table WHERE id=:id")
//    suspend fun delete(id:Int)

    //Get a user with a given username
    @Query("SELECT * FROM user_table WHERE username=:username")
    fun getUser(username:String) : Flow<User>

    @Query("SELECT * FROM workout_table WHERE date=:date")
    fun getWorkoutByDate(date: String) : Flow<Workout>

    // Find if username is taken
    @Query("SELECT COUNT(*) FROM user_table WHERE username=:username")
    fun isUsernameTaken(username: String) : Flow<Int>



}