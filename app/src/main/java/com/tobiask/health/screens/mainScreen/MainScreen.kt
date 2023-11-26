package com.tobiask.health.screens.mainScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tobiask.health.ProgressCircle
import com.tobiask.health.navigation.Screen
import com.tobiask.health.R
import com.tobiask.health.database.DAO
import com.tobiask.health.database.Goals
import com.tobiask.health.database.Stats
import com.tobiask.health.screens.water_screen.WaterScreenViewModel
import java.time.LocalDate
import java.time.LocalTime


@Composable
fun DashboardScreen(navController: NavController, dao: DAO) {
    LocalContext.current
    val viewModel = viewModel<MainScreenViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainScreenViewModel(dao) as T
            }
        }
    )

    viewModel.reset()

    val full = remember { mutableStateOf(true) }
    val goals = viewModel.dao.getGoals(1).collectAsState(initial = Goals(id = 2, date = ""))
    val stats = viewModel.dao.getStats(1).collectAsState(initial = listOf(Stats(id = 2, date = "")))
    if (goals.value.id == 2) {
        viewModel.insertGoals()
        full.value = false
    }

    if (goals.value.id != 2 && goals.value.date != "") {
        if (LocalDate.parse(goals.value.date).isBefore(LocalDate.now())) {
            viewModel.deleteAllStats(goals.value)
        }
        if (LocalDate.parse(goals.value.date).monthValue != LocalDate.now().monthValue) {
            viewModel.deleteAllStatsMonthly(goals.value)
        }
        full.value = true
    }
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(top = 15.dp), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(250.dp)
                    .wrapContentWidth(Alignment.Start)
            ) {
                ProgressCircle(
                    percentage = if (goals.value.id != 2) (goals.value.workoutsProgress.toFloat() / goals.value.workouts).toFloat() else 0f,
                    number = if (goals.value.id != 2) goals.value.workouts.toDouble() else 0.0,
                    color = Color(0xffc4342d),
                    colorTrans = Color(0x8fc4342d),
                    radius = 120.dp,
                    textColor = MaterialTheme.colorScheme.onBackground,
                    description = stringResource(id = R.string.workouts),
                    onClick = {
                              navController.navigate(Screen.WorkoutScreen.route)
                    },
                    onLongClick = {}
                )
            }
        }
        Spacer(modifier = Modifier.height(25.dp))
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(210.dp)
                    .wrapContentWidth(Alignment.Start)
            ) {
                ProgressCircle(
                    percentage = if (goals.value.id != 2) (goals.value.caloriesProgress.toFloat() / goals.value.calories) else 0f,
                    number = if (goals.value.id != 2) goals.value.calories.toDouble() else 0.0,
                    color = Color(0xffff8c00),
                    colorTrans = Color(0x8fff8c00),
                    radius = 90.dp,
                    textColor = MaterialTheme.colorScheme.onBackground,
                    description = stringResource(id = R.string.calories),
                    onClick = {navController.navigate(Screen.CaloriesScreen.route)},
                    onLongClick = {}
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(210.dp)
                    .wrapContentWidth(Alignment.Start)
            ) {
                ProgressCircle(
                    percentage = if(goals.value.id != 2) (goals.value.waterProgress.toFloat() / goals.value.water) else 0f,
                    number = if(goals.value.id != 2) goals.value.water.toDouble() else 0.0,
                    color = Color(0xff4cb9fa),
                    colorTrans = Color(0x8f4cb9fa),
                    radius = 90.dp,
                    textColor = MaterialTheme.colorScheme.onBackground,
                    description = stringResource(id = R.string.water),
                    onClick = {
                        navController.navigate(Screen.WaterScreen.route)
                    },
                    onLongClick = {}
                )
            }
        }
        Column(Modifier.fillMaxSize(0.5f), verticalArrangement = Arrangement.Center) {
            Button(onClick = { viewModel.deleteAllStatsMonthly(goals.value) }) {
                Text(text = "Reset")
            }
        }
    }
}

