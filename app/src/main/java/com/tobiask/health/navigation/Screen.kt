package com.tobiask.health.navigation

sealed class Screen(val route: String){
    object DashboardScreen:Screen("dashboard_screen")
    object WaterScreen:Screen("water_screen")
}
