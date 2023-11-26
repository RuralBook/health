package com.tobiask.health.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
sealed interface DAO{
    @Update(Stats::class)
    suspend fun updateStats(stats: Stats)

    @Insert(Stats::class)
    suspend fun insertStats(stats: Stats)

    @Delete(Stats::class)
    suspend fun deleteStats(stats: Stats)

    @Update(Goals::class)
    suspend fun updateGoals(goals: Goals)

    @Insert(Goals::class)
    suspend fun insertGoals(goals: Goals)

    @Query("SELECT * FROM goals WHERE id LIKE :id")
    fun getGoals(id: Int): Flow<Goals>

    @Query("SELECT * FROM stats WHERE id LIKE :id")
    fun getStats(id: Int): Flow<List<Stats>>

    @Query("SELECT * FROM stats WHERE water > 0.0")
    fun getWaterStats(): Flow<List<Stats>>

    @Query("SELECT * FROM stats WHERE calories > 0.0")
    fun getCaloriesStats(): Flow<List<Stats>>

    @Query("SELECT * FROM stats WHERE workouts > 0.0")
    fun getWorkoutStats(): Flow<List<Stats>>

    @Query("Delete FROM stats")
    suspend fun delAllStats()

    @Query("DELETE FROM Goals WHERE id != 1")
    suspend fun resetAllGoals()


}