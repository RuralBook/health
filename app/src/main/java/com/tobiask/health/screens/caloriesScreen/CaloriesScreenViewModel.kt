package com.tobiask.health.screens.caloriesScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.health.database.DAO
import com.tobiask.health.database.Goals
import com.tobiask.health.database.Stats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CaloriesScreenViewModel(val dao: DAO): ViewModel() {

    private val _addDrink = MutableStateFlow(false)
    val addDrink = _addDrink.asStateFlow()

    fun popUpAddDrink(){
        _addDrink.value = !_addDrink.value
    }

    private val _changeGoal = MutableStateFlow(false)
    val changeGoal = _changeGoal.asStateFlow()

    fun popUpChangeGoal(){
        _changeGoal.value = !_changeGoal.value
    }


    fun getStats(stats: Stats){
        viewModelScope.launch {
            dao.getStats(stats.id)
        }
    }

    fun updateCaloriesStats(stats: Stats, goals: Goals){
        viewModelScope.launch {
            dao.insertStats(stats)
            dao.updateGoals(goals)
        }
    }

    fun updateGoals(goals: Goals){
        viewModelScope.launch {
            dao.updateGoals(goals)
        }
    }

    fun deleteCaloriesStats(stats: Stats){
        viewModelScope.launch{
            dao.deleteStats(stats)
        }
    }

}