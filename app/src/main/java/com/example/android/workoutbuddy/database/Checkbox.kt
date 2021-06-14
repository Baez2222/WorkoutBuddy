package com.example.android.workoutbuddy.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checkbox_table")
class Checkbox(@ColumnInfo(name="currCheckbox") val currCheckbox: Int,
               @ColumnInfo(name="timerHolderPosition") val timerHolderPosition: Int,
               @ColumnInfo(name="tag") val tag:String,
               @ColumnInfo(name="exercise") val exercise:String,
               @ColumnInfo(name="workoutName") val workoutName:String,
               @PrimaryKey@ColumnInfo(name="id") val id: Int){
}