package com.example.android.workoutbuddy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.workoutbuddy.Picture
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDAO {

    //Get all dreams; sort the results by id
    @Query("SELECT * FROM user_table")
    fun getAllUsers() : Flow<List<User>>

    @Query("SELECT * FROM workout_table")
    fun getAllWorkouts() : Flow<List<Workout>>

    @Query("SELECT * FROM exercise_table")
    fun getAllExercises(): Flow<List<Exercise>>

    //Insert a dream; if there is conflict, we ignore the insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkout(workout: Workout)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercise(exercise: Exercise)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFood(food: Food)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(picture: Picture)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertRecyclerInformation(recyclerInformation: RecyclerInformation) // save recycler view state

    //Update a dream with a given id and a set of fields
//    @Query("UPDATE dream_table SET title=:title, content=:content, reflection=:reflection, emotion=:emotion WHERE id=:id")
//    suspend fun update(title:String, content:String, reflection:String, emotion:String, id:Int)

    //Delete a dream with a given id
//    @Query("DELETE FROM dream_table WHERE id=:id")
//    suspend fun delete(id:Int)

    // get recycler view state
//    @Query("SELECT * FROM recycler_table where id=:id")
//    fun getRecyclerInformation(id:String): Flow<RecyclerInformation>

    //Get a user with a given username
    @Query("SELECT * FROM user_table WHERE username=:username")
    fun getUser(username:String) : Flow<User>

    // Get all workouts of a user
    @Query("SELECT workout FROM workout_table WHERE username=:username GROUP BY workout")
    fun getWorkoutsByUsername(username: String) : Flow<List<String>>

    // Get workout of user by workout name
    @Query("SELECT * FROM workout_table WHERE username=:username AND workout=:workout")
    fun getWorkoutByWorkoutName(username: String, workout: String) : Flow<List<Workout>>

    // Get workout/exercise by date
    @Query("SELECT * FROM exercise_table WHERE username=:username AND date=:date ORDER BY workout")
    fun getWorkoutsByDate(username: String, date: String) : Flow<List<Exercise>>

    // Get number of sets by exercise, date, username
    @Query("SELECT COUNT(*) FROM exercise_table WHERE username=:username AND date=:date AND exercise=:exercise")
    fun getExerciseSetCount(username: String, date: String, exercise: String): Flow<Int>

    // update checkboxstate
//    @Query("UPDATE workout_table SET checkboxState=:checkboxState WHERE username=:username AND exercise=:exercise AND workout=:workout")
//    suspend fun updateCheckBoxState(checkboxState: String, username: String, exercise: String, workout: String)

    // insert checkboxstate
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckboxState(checkboxState: CheckboxState)
    // update checkboxstate
    @Query("UPDATE checkboxstate_table SET checkboxState=:checkboxState WHERE username=:username AND workout=:workout")
    suspend fun updateCheckBoxState(checkboxState: String, username: String, workout: String)

    @Query("SELECT * FROM checkboxstate_table WHERE username=:username AND workout=:workout")
    fun getCheckBoxState(username: String, workout: String): Flow<CheckboxState>

    // update timeLeft
    @Query("UPDATE workout_table SET timeLeft=:timeLeft WHERE username=:username AND exercise=:exercise AND workout=:workout")
    suspend fun updateTimeLeft(timeLeft: Long, username: String, exercise: String, workout: String)

//    @Query("SELECT * FROM workout_table WHERE date=:date")
//    fun getWorkoutByDate(date: String) : Flow<Workout>

    // Find if username is taken
    @Query("SELECT COUNT(*) FROM user_table WHERE username=:username")
    fun isUsernameTaken(username: String) : Flow<Int>

    // Get current calorie intake
    @Query("SELECT calorieIntake FROM user_table WHERE username=:username")
    fun getCalorieIntake(username: String): Flow<Int>

    // update calorie intake
    @Query("UPDATE user_table SET calorieIntake=:calorieIntake WHERE username=:username")
    suspend fun updateCalorieIntake(calorieIntake: Int, username: String)

    // get list of food from that day
    @Query("SELECT * FROM food_table WHERE date=:date AND username=:username ORDER BY id")
    fun getFoodListByDate(date: String, username: String) : Flow<List<Food>>

    // get picture file path
    @Query("SELECT * FROM picture_table WHERE date=:date AND username=:username")
    fun getPicture(date: String, username: String) : Flow<Picture>

    // get checkbox
    @Query("SELECT * FROM checkbox_table")
    fun getAllCheckbox() : Flow<List<Checkbox>>

    // insert checkbox
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckbox(checkbox: Checkbox)

    // set checkbox
    @Query("UPDATE checkbox_table SET currCheckbox=:currCheckbox AND checkboxId=:checkboxId WHERE id=1")
    fun updateCheckbox(currCheckbox: Int, checkboxId:Int)



}