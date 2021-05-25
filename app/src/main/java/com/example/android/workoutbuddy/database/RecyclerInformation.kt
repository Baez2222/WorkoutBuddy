package com.example.android.workoutbuddy.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "recycler_table")
class RecyclerInformation(@PrimaryKey@ColumnInfo(name="id") val id:String,
                          @ColumnInfo(name="layout") val layout: String)

//@Entity(tableName = "user_table")
//class User(@PrimaryKey @ColumnInfo(name="username") val title: String,
//           @ColumnInfo(name="password") val content: Int,
//           @ColumnInfo(name="CalorieIntake") val calorieIntake: Int = 2020) {
//}