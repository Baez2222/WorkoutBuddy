package com.example.android.workoutbuddy.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checkbox_table")
class Checkbox(@ColumnInfo(name="currCheckbox") val currCheckbox: Int,
               @ColumnInfo(name="checkboxId") val checkboxId: Int,
               @PrimaryKey@ColumnInfo(name="id") val id: Int){
}