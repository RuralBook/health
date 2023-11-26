package com.tobiask.health.navigation

sealed class Screen(val route: String){
    object DashboardScreen:Screen("dashboard_screen")
    object WaterScreen:Screen("water_screen")
    object CaloriesScreen:Screen("calories_screen")
    object WorkoutScreen: Screen("workout_screen")
}
