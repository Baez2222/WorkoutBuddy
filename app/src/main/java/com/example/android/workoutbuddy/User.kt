package com.example.android.workoutbuddy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity -> class represents a SQLite table
@Entity(tableName = "user_table")
class User(@PrimaryKey@ColumnInfo(name="username") val title: String,
           @ColumnInfo(name="password") val content: Int,
           @ColumnInfo(name="CalorieIntake") val calorieIntake: Int = 2020) {
}
