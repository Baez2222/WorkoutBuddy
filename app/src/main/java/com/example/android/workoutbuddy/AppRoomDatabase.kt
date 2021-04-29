package com.example.android.workoutbuddy

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Workout::class], version = 1, exportSchema = false)
public abstract class AppRoomDatabase : RoomDatabase() {
    // connects with DAO
    abstract fun appDAO():AppDAO // getter

    companion object{

        @Volatile // singleton
        private var INSTANCE:AppRoomDatabase? = null

        fun getDatabase(context : Context): AppRoomDatabase{

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