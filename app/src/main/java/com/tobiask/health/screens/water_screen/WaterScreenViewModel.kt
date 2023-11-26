package com.tobiask.health.screens.water_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.health.database.DAO
import com.tobiask.health.database.Goals
import com.tobiask.health.database.Stats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WaterScreenViewModel(val dao: DAO): ViewModel() {

    private val _addDrink = MutableStateFlow(false)
    val addDrink = _addDrink.asStateFlow()

    fun popUpAddDrink(){
        _addDrink.value = !_addDrink.value
    }


    fun getStats(stats: Stats){
        viewModelScope.launch {
            dao.getStats(stats.id)
        }
    }

    fun updateWaterStats(stats: Stats, goals: Goals){
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

    fun deleteWaterStats(stats: Stats){
        viewModelScope.launch{
            dao.deleteStats(stats)
        }
    }

}