package com.tobiask.health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.tobiask.health.database.Database
import com.tobiask.health.navigation.Navigation
import com.tobiask.health.navigation.Screen
import com.tobiask.health.screens.mainScreen.DashboardScreen
import com.tobiask.health.screens.water_screen.WaterScreen
import com.tobiask.health.ui.theme.HealthTheme
import kotlinx.coroutines.CoroutineScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthTheme {
                val context = LocalContext.current
                val db by lazy {
                    Room.databaseBuilder(
                        context = context,
                        Database::class.java,
                        "HealthCards.db"
                    ).fallbackToDestructiveMigration().build()
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screen.DashboardScreen.route){
                        composable(route = Screen.DashboardScreen.route){
                            DashboardScreen(navController, db.dao)
                        }
                        composable(route = Screen.WaterScreen.route){
                            WaterScreen(db.dao)
                        }
                    }
                }
            }
        }
    }
}