package com.example.android.workoutbuddy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "picture_table", primaryKeys = ["date","username"])
class Picture (@ColumnInfo(name="username") val username:String,
               @ColumnInfo(name="photoFile") val photoFile:String,
               @ColumnInfo(name="date") val date:String)

//@Entity(tableName = "user_table")
//class User(@PrimaryKey @ColumnInfo(name="username") val title: String,
//           @ColumnInfo(name="password") val content: Int,
//           @ColumnInfo(name="CalorieIntake") val calorieIntake: Int = 2020) {
//}