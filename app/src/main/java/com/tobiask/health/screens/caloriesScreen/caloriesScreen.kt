package com.tobiask.health.screens.caloriesScreen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tobiask.health.ProgressCircle
import com.tobiask.health.R
import com.tobiask.health.database.DAO
import com.tobiask.health.database.Goals
import com.tobiask.health.database.Stats
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CaloriesScreen(dao: DAO) {

    val viewModel = viewModel<CaloriesScreenViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CaloriesScreenViewModel(dao) as T
            }
        }
    )

    val goals = viewModel.dao.getGoals(1).collectAsState(initial = Goals(id = 2, date = ""))
    val stats = viewModel.dao.getCaloriesStats().collectAsState(initial = listOf(Stats(id = 2, date = "")))
    val popUp = viewModel.addDrink.collectAsState()
    val popUpChangeGoal = viewModel.changeGoal.collectAsState()
    if (goals.value.id  != 2) {
        if (popUp.value){
            AddMeal(viewModel = viewModel, goals.value)
        }

        if (popUpChangeGoal.value){
            ChangeCaloriesGoal(viewModel = viewModel, goals = goals.value)
        }

        Scaffold(Modifier.fillMaxSize(), topBar = {
            TopAppBar(title = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = stringResource(id = R.string.calories), fontSize = 30.sp)
                }
            })
        }, floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.popUpAddDrink()
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }) { innerPadding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(300.dp)
                            .wrapContentWidth(Alignment.Start)
                    ) {
                        ProgressCircle(percentage = (goals.value.caloriesProgress.toFloat() / goals.value.calories),
                            number = goals.value.calories.toDouble(),
                            color = Color(0xff4cb9fa),
                            colorTrans = Color(0x8f4cb9fa),
                            radius = 140.dp,
                            textColor = MaterialTheme.colorScheme.onBackground,
                            description = "",
                            onClick = {},
                            onLongClick = {
                                viewModel.popUpChangeGoal()
                            })
                    }
                }
                LazyColumn(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
                    itemsIndexed(stats.value){_, row ->
                        RevealSwipe(
                            modifier = Modifier.padding(top = 10.dp),
                            directions = setOf(
                                RevealDirection.EndToStart
                            ),
                            hiddenContentEnd = {
                                Icon(modifier = Modifier.padding(end = 15.dp),imageVector = Icons.Default.Delete, contentDescription = "delete", tint = MaterialTheme.colorScheme.onSecondary)
                            },
                            backgroundCardEndColor = MaterialTheme.colorScheme.secondary,
                            onBackgroundEndClick = {
                                viewModel.deleteCaloriesStats(row)
                                viewModel.updateGoals(Goals(
                                    id = goals.value.id,
                                    calories = goals.value.calories,
                                    caloriesProgress = goals.value.caloriesProgress - row.calories.roundToInt(),
                                    date = goals.value.date
                                ))
                            }
                        ) {
                            CaloriesItem(date = row.date, amount = row.calories.toInt(), percentage = ((row.calories / goals.value.calories)*100))
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }

            }
        }
    }
}


@Composable
fun CaloriesItem(
    date: String,
    amount: Int,
    percentage: Double
) {
    ElevatedCard(
        modifier =
        Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
        , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = date, fontWeight = FontWeight.SemiBold)
                Text(text = "${amount}ML", fontWeight = FontWeight.Bold)
                Text(text = "~ ${percentage.roundToInt()}%")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMeal(viewModel: CaloriesScreenViewModel, goals: Goals){
    val caloriesAmount = remember{ mutableStateOf(TextFieldValue("150"))}
    AlertDialog(onDismissRequest = {
        viewModel.popUpAddDrink()
    },
        confirmButton = {
            Button(onClick = {
                viewModel.updateCaloriesStats(
                    Stats(
                        calories = caloriesAmount.value.text.toDouble(),
                        date = LocalTime.now().withNano(0).withSecond(0).toString()
                    ),
                    Goals(
                        1,
                        water = goals.water,
                        waterProgress = goals.waterProgress,
                        calories = goals.calories,
                        caloriesProgress = goals.caloriesProgress + caloriesAmount.value.text.toInt(),
                        workouts = goals.workouts,
                        workoutsProgress = goals.workoutsProgress,
                        date = LocalDate.now().toString(),
                    )
                )
                viewModel.popUpAddDrink()
            }) {
                Text("ADD")
            }
        },
        title = {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Text(text = "ADD MEAL", fontSize = 20.sp)
            }

        },
        text = {
            Column {
                TextField(
                    value = caloriesAmount.value,
                    onValueChange = { caloriesAmount.value = it },
                    label = { Text(text = "calories amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeCaloriesGoal(viewModel: CaloriesScreenViewModel, goals: Goals){
    val caloriesAmount = remember{ mutableStateOf(TextFieldValue(goals.calories.toString()))}
    AlertDialog(onDismissRequest = {
        viewModel.popUpChangeGoal()
    },
        confirmButton = {
            Button(onClick = {
                viewModel.updateGoals(
                    Goals(
                        1,
                        water = goals.water,
                        waterProgress = goals.waterProgress,
                        calories = caloriesAmount.value.text.toDouble().roundToInt(),
                        caloriesProgress = goals.caloriesProgress,
                        workouts = goals.workouts,
                        workoutsProgress = goals.workoutsProgress,
                        date = LocalDate.now().toString(),
                    )
                )
                viewModel.popUpChangeGoal()
            }) {
                Text("ADD")
            }
        },
        title = {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Text(text = "Change goal", fontSize = 20.sp)
            }

        },
        text = {
            Column {
                TextField(
                    value = caloriesAmount.value,
                    onValueChange = { caloriesAmount.value = it },
                    label = { Text(text = "calories amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}