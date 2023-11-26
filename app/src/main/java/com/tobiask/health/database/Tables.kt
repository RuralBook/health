package com.tobiask.health.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Goals(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val water: Int = 2000,
    val waterProgress: Int = 0,
    val calories: Double = 2500.0,
    val caloriesProgress: Double = 0.0,
    val workouts: Int = 5,
    val workoutsProgress: Double = 0.0,

    val date: String
)

@Entity
data class Stats(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val waterType: String = "water",
    val water: Double = 0.0,
    val calories: Double = 0.0,
    val workouts: Int = 0,
    val date: String
)
