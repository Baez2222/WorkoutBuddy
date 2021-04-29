package com.example.android.workoutbuddy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_table")
class Workout(@ColumnInfo(name="workout") val workout: String,
              @ColumnInfo(name="exercise") val exercise: String,
              @ColumnInfo(name="sets") val sets: Int,
              @ColumnInfo(name="reps") val reps: Int,
              @ColumnInfo(name="rest") val rest: Int,
              @ColumnInfo(name="date") val date: String,
              @ColumnInfo(name="username") val username: String) {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="id") var id:Int=0
}