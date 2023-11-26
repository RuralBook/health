package com.tobiask.health.screens.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.health.database.DAO
import com.tobiask.health.database.Goals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainScreenViewModel(val dao: DAO) : ViewModel() {

    fun insertGoals() {
        viewModelScope.launch { dao.insertGoals(Goals(date= "")) }
    }

    fun deleteAllStats(goals: Goals){
        viewModelScope.launch { dao.delAllStats(); dao.updateGoals(Goals(
            id = 1,
            water = goals.water,
            calories = goals.calories,
            workouts = goals.workouts,
            workoutsProgress = goals.workoutsProgress,
            date = LocalDate.now().toString())
        )
        }
    }
    fun deleteAllStatsMonthly(goals: Goals){
        viewModelScope.launch { dao.delAllStats(); dao.updateGoals(Goals(
            id = 1,
            water = goals.water,
            calories = goals.calories,
            workouts = goals.workouts,
            workoutsProgress = 0,
            date = LocalDate.now().toString())
        )
        }
    }

    fun reset(){
        viewModelScope.launch { dao.resetAllGoals();}
    }
}