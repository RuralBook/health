package com.tobiask.health.screens.water_screen

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
fun WaterScreen(dao: DAO) {

    val context = LocalContext.current


    val viewModel = viewModel<WaterScreenViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WaterScreenViewModel(dao) as T
            }
        }
    )

    val goals = viewModel.dao.getGoals(1).collectAsState(initial = Goals(id = 2, date = ""))
    val stats = viewModel.dao.getWaterStats().collectAsState(initial = listOf(Stats(id = 2, date = "")))
    val popUp = viewModel.addDrink.collectAsState()
    if (goals.value.id  != 2) {

        if (popUp.value){
            AddWater(viewModel = viewModel, goals.value)
        }

        Scaffold(Modifier.fillMaxSize(), topBar = {
            TopAppBar(title = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = stringResource(id = R.string.water), fontSize = 30.sp)
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
                        ProgressCircle(percentage = (goals.value.waterProgress.toFloat() / goals.value.water),
                            number = goals.value.water.toDouble(),
                            color = Color(0xff4cb9fa),
                            colorTrans = Color(0x8f4cb9fa),
                            radius = 140.dp,
                            textColor = MaterialTheme.colorScheme.onBackground,
                            description = "",
                            onClick = {
                                //Log.d("water", goals.value.waterProgress.toDouble().toString() + "|" + goals.value.water.toDouble().toString() + "|" + ((goals.value.waterProgress.toFloat() / goals.value.water)*100f).toString())
                            },
                            onLongClick = {})
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
                                viewModel.deleteWaterStats(row)
                                viewModel.updateGoals(Goals(
                                    id = goals.value.id,
                                    water = goals.value.water,
                                    waterProgress = goals.value.waterProgress - row.water.roundToInt(),
                                    date = goals.value.date
                                ))
                            }
                        ) {
                            WaterItem(date = row.date, amount = row.water.toInt(), type = row.waterType, percentage = ((row.water / goals.value.water)*100))
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
fun WaterItem(
    date: String,
    amount: Int,
    type: String,
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
                Text(text = type)
                Text(text = "${percentage}%")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWater(viewModel: WaterScreenViewModel, goals: Goals){
    val waterAmount = remember{ mutableStateOf(TextFieldValue())}
    val type = remember{ mutableStateOf(TextFieldValue())}
    AlertDialog(onDismissRequest = {
        viewModel.popUpAddDrink()
        },
        confirmButton = {
                Button(onClick = {
                    viewModel.updateWaterStats(
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
                    viewModel.popUpAddDrink()
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
                    label = { Text(text = "water amount") }
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