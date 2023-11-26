package com.tobiask.health

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt


@SuppressLint("RememberReturnType")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProgressCircle(
    percentage: Float,
    number: Double,
    color: Color,
    colorTrans: Color,
    radius: Dp,
    fontSize: TextUnit = 28.sp,
    strokeWidth: Dp = 16.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0,
    textColor: Color,
    description: String?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    var colorTransparent = colorTrans

    if(percentage >= 1f){colorTransparent = color}
    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        ), label = ""
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(radius * 2f)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
        ) {
            Canvas(
                modifier = Modifier
                    .size(radius * 2f)

            ) {
                drawArc(
                    color = color,
                    -90f,
                    360 * curPercentage.value,
                    useCenter = false,
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = colorTransparent, // Fill the whole arc with black color
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Fill,
                )
            }
            Text(
                text = (curPercentage.value * number).roundToInt().toString(), //(curPercentage.value * number).toInt().toShort().toString() + "\n /" + number.toInt().toString(),
                color = textColor,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Log.d("water", number.toDouble().toString())
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = (description.toString()),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = fontSize * 0.75,
        )
    }

}