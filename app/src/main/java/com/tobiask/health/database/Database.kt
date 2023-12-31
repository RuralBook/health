package com.tobiask.health.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Stats::class, Goals::class],
    version = 2
)
abstract class Database : RoomDatabase() {
    abstract val dao: DAO
}