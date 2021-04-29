package com.example.android.workoutbuddy

import android.app.Application

class AppApplication : Application() {
    // creating 1 instance of database
    // 1 instance of repository

    // lazy -> the value gets computed or executes only upon first access
    val database by lazy { AppRoomDatabase.getDatabase(this) }
    val repository by lazy { AppRepository(database.appDAO()) }
}