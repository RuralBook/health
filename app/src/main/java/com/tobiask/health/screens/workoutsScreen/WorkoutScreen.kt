package com.tobiask.health.screens.workoutsScreen

import android.util.Log
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
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun WorkoutScreen(dao: DAO) {

    val context = LocalContext.current


    val viewModel = viewModel<WorkoutScreenViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WorkoutScreenViewModel(dao) as T
            }
        }
    )

    val goals = viewModel.dao.getGoals(1).collectAsState(initial = Goals(id = 2, date = ""))
    val stats = viewModel.dao.getWorkoutStats().collectAsState(initial = listOf(Stats(id = 2, date = "")))
    val popUp = viewModel.addDrink.collectAsState()
    val popUpChangeGoal = viewModel.changeGoal.collectAsState()
    if (goals.value.id  != 2) {

        /*if (popUp.value){
            AddWorkout(viewModel = viewModel, goals.value)
        }*/

        if (popUpChangeGoal.value){
            ChangeWorkoutGoal(viewModel = viewModel, goals = goals.value)
        }

        Scaffold(Modifier.fillMaxSize(), topBar = {
            TopAppBar(title = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = stringResource(id = R.string.water), fontSize = 30.sp)
                }
            })
        }, floatingActionButton = {
            FloatingActionButton(onClick = {
                //viewModel.popUpAddWorkout()
                viewModel.updateWorkoutStats(
                    Stats(
                        workouts = 1.0,
                        date = LocalTime.now().withNano(0).withSecond(0).toString()
                    ),
                    Goals(
                        1,
                        water = goals.value.water,
                        waterProgress = goals.value.waterProgress,
                        calories = goals.value.calories,
                        caloriesProgress = goals.value.caloriesProgress,
                        workouts = goals.value.workouts,
                        workoutsProgress = goals.value.workoutsProgress + 1,
                        date = LocalDate.now().toString(),
                    )
                )
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
                        ProgressCircle(
                            percentage = (goals.value.workoutsProgress.toFloat() / goals.value.workouts),
                            number = goals.value.workouts.toDouble(),
                            color = Color(0xffc4342d),
                            colorTrans = Color(0x8fc4342d),
                            radius = 140.dp,
                            textColor = MaterialTheme.colorScheme.onBackground,
                            description = "",
                            onClick = {
                                //Log.d("water", goals.value.waterProgress.toDouble().toString() + "|" + goals.value.water.toDouble().toString() + "|" + ((goals.value.waterProgress.toFloat() / goals.value.water)*100f).toString())
                            },
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
                                viewModel.deleteWorkoutStats(row)
                                viewModel.updateGoals(Goals(
                                    1,
                                    water = goals.value.water,
                                    waterProgress = goals.value.waterProgress,
                                    calories = goals.value.calories,
                                    caloriesProgress = goals.value.caloriesProgress,
                                    workouts = goals.value.workouts,
                                    workoutsProgress = goals.value.workoutsProgress - 1,
                                    date = LocalDate.now().toString(),
                                ))
                            },
                            closeOnBackgroundClick = true,
                            enableSwipe = true
                        ) {
                            WorkoutItem(date = row.date, percentage = ((row.workouts / goals.value.workouts)*100))
                            //Log.d("percentage", ((row.water / goals.value.water)).toString())
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }

            }
        }
    }
}


@Composable
fun WorkoutItem(
    date: String,
    //amount: Int,
    //type: String,
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
                //Text(text = "${amount}ML", fontWeight = FontWeight.Bold)
                Text(text = "Workout")
                Text(text = "~ ${percentage.roundToInt()}%")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkout(viewModel: WorkoutScreenViewModel, goals: Goals){
    val waterAmount = remember{ mutableStateOf(TextFieldValue("150"))}
    val type = remember{ mutableStateOf(TextFieldValue("Water"))}
    AlertDialog(onDismissRequest = {
        viewModel.popUpAddWorkout()
    },
        confirmButton = {
            Button(onClick = {
                viewModel.updateWorkoutStats(
                    Stats(
                        water = waterAmount.value.text.toDouble(),
                        waterType = type.value.text,
                        date = LocalTime.now().withNano(0).withSecond(0).toString()
                    ),
                    Goals(
                        1,
                        water = goals.water,
                        waterProgress = goals.waterProgress + waterAmount.value.text.toInt(),
                        calories = goals.calories,
                        caloriesProgress = goals.caloriesProgress,
                        workouts = goals.workouts,
                        workoutsProgress = goals.workoutsProgress,
                        date = LocalDate.now().toString(),
                    )
                )
                viewModel.popUpAddWorkout()
            }) {
                Text("ADD")
            }
        },
        title = {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Text(text = "ADD DRINK", fontSize = 20.sp)
            }

        },
        text = {
            Column {
                TextField(
                    value = waterAmount.value,
                    onValueChange = { waterAmount.value = it },
                    label = { Text(text = "water amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = type.value,
                    onValueChange = { type.value = it },
                    label = { Text(text = "drink type") }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeWorkoutGoal(viewModel: WorkoutScreenViewModel, goals: Goals){
    val workouts = remember{ mutableStateOf(TextFieldValue(goals.water.toString()))}
    AlertDialog(onDismissRequest = {
        viewModel.popUpChangeGoal()
    },
        confirmButton = {
            Button(onClick = {
                viewModel.updateGoals(
                    Goals(
                        1,
                        water = goals.water ,
                        waterProgress = goals.waterProgress,
                        calories = goals.calories,
                        caloriesProgress = goals.caloriesProgress,
                        workouts = workouts.value.text.toInt(),
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
                    value = workouts.value,
                    onValueChange = { workouts.value = it },
                    label = { Text(text = "workouts") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}