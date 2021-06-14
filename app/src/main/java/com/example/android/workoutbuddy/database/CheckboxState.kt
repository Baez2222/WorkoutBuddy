package com.example.android.workoutbuddy.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "checkboxstate_table",primaryKeys = ["workout", "username"])
class CheckboxState(@ColumnInfo(name="username") val username: String,
                    @ColumnInfo(name="workout") val workout: String,
                    @ColumnInfo(name="checkboxState") val checkboxState: String,
                    @ColumnInfo(name="weightState") val weightState: String,
                    @ColumnInfo(name="repsState") val repsState: String)