package com.example.android.workoutbuddy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_table")
class Food (@ColumnInfo(name="username") val username: String,
            @ColumnInfo(name="description") val description:String,
            @ColumnInfo(name="calories") val calories:Int,
            @ColumnInfo(name="foodId") val foodId:Int,
            @ColumnInfo(name="date") val date:String,
            @ColumnInfo(name="quantity") val quantity:Int){
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id:Int=0
}