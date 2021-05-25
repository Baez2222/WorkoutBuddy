package com.example.android.workoutbuddy.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "workout_table", primaryKeys = ["workout","exercise", "username"])
class Workout(@ColumnInfo(name="workout") val workout: String,
              @ColumnInfo(name="exercise") val exercise: String,
              @ColumnInfo(name="sets") val sets: Int,
              @ColumnInfo(name="reps") val reps: Int,
              @ColumnInfo(name="rest") val rest: Int,
              @ColumnInfo(name="weight") val weight: Int,
              @ColumnInfo(name="username") val username: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(workout)
        parcel.writeString(exercise)
        parcel.writeInt(sets)
        parcel.writeInt(reps)
        parcel.writeInt(rest)
        parcel.writeInt(weight)
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Workout> {
        override fun createFromParcel(parcel: Parcel): Workout {
            return Workout(parcel)
        }

        override fun newArray(size: Int): Array<Workout?> {
            return arrayOfNulls(size)
        }
    }
}