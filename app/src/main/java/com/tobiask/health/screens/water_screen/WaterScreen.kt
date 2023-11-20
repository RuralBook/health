package com.tobiask.health.screens.water_screen

import android.content.res.Configuration
import android.graphics.fonts.FontStyle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
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

    val goals = viewModel.dao.getGoals(1).collectAsState(initial = listOf(Goals(id = 2, date = "")))
    val stats = viewModel.dao.getWaterStats().collectAsState(initial = listOf(Stats(id = 2, date = "")))
    if (goals.value.isNotEmpty()) {

        Scaffold(Modifier.fillMaxSize(), topBar = {
            TopAppBar(title = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = stringResource(id = R.string.water), fontSize = 30.sp)
                }
            })
        }, floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.updateWaterStats(
                    Stats(
                        water = 250.0,
                        date = LocalTime.now().withNano(0).withSecond(0).toString()
                    ),
                    Goals(
                        1,
                        water = goals.value[0].water,
                        waterProgress = goals.value[0].waterProgress + 250.0,
                        calories = goals.value[0].calories,
                        caloriesProgress = goals.value[0].caloriesProgress,
                        workouts = goals.value[0].workouts,
                        workoutsProgress = goals.value[0].workoutsProgress,
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
                        ProgressCircle(percentage = (goals.value[0].waterProgress / goals.value[0].water).toFloat(),
                            number = goals.value[0].water,
                            color = Color(0xffc4342d),
                            colorTrans = Color(0x8fc4342d),
                            radius = 140.dp,
                            textColor = MaterialTheme.colorScheme.onBackground,
                            description = "",
                            onClick = {
                            },
                            onLongClick = {})
                    }
                }
                LazyColumn() {
                    itemsIndexed(stats.value){_, row ->
                        WaterItem(date = row.date, amount = row.water, type = "Water", percentage = .25)
                    }
                }

            }
        }
    }
}


@Composable
fun WaterItem(
    date: String,
    amount: Double,
    type: String,
    percentage: Double
) {
    ElevatedCard(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(15.dp), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = date, fontWeight = FontWeight.SemiBold)
                Text(text = "${amount.toInt()}ML", fontWeight = FontWeight.Bold)
                Text(text = "Wsser")
                Text(text = "${percentage * 100}%")
            }
        }
    }
}