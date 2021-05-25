package com.example.android.workoutbuddy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.workoutbuddy.Picture

@Database(entities = [User::class, Workout::class, Exercise::class, Food::class, Picture::class], version = 3, exportSchema = false)
public abstract class AppRoomDatabase : RoomDatabase() {
    // connects with DAO
    abstract fun appDAO(): AppDAO // getter

    companion object{

        @Volatile // singleton
        private var INSTANCE: AppRoomDatabase? = null

        fun getDatabase(context : Context): AppRoomDatabase {

            return INSTANCE ?: synchronized(this){

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}