package com.example.entainneds.ui

import androidx.compose.foundation.background
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.entainneds.R
import com.example.entainneds.backend.RaceSummary
import com.example.entainneds.ui.theme.Purple80
import kotlinx.coroutines.delay
import java.time.Duration
import kotlin.math.absoluteValue

@Composable
fun RaceSummaryScreen(modifier: Modifier = Modifier, raceSummaryViewModel: RaceSummaryViewModel = viewModel()) {
    val raceSummaryModel by raceSummaryViewModel.uiState.collectAsState()
    RaceSummaryView(modifier = modifier, raceSummaryModel = raceSummaryModel)
    LaunchedEffect(Unit) {
        delay(60 * 1000)
        raceSummaryViewModel.fetchRaceSummaries()
    }
}

@Composable
fun RaceSummaryView(modifier: Modifier = Modifier, raceSummaryModel: RaceSummaryModel) {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis() / 1000) }
    Box(modifier = modifier
        .fillMaxSize()) {
        /** If there is an error, show error, otherwise show the list of races **/
        raceSummaryModel.error?.let {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(dimensionResource(id = R.dimen.spacing_2))
                    .testTag("Error"),
                text = it,
                fontSize = 25.sp,
                color = Purple80,
            )
        } ?:
        raceSummaryModel.raceSummaries.takeIf { it.isNotEmpty() }?.let {
            RaceSummaryList(modifier = Modifier.testTag("RaceSummaryList"), currentTime = currentTime.absoluteValue / 1000, raceSummaries = it)
        }
        if (raceSummaryModel.loading && raceSummaryModel.raceSummaries.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag("LoadingIndicator"),
            )
        }
    }
    LaunchedEffect(currentTime) {
        delay(1000)
        currentTime = System.currentTimeMillis()
    }
}

@Composable
fun RaceSummaryList(currentTime: Long, modifier: Modifier = Modifier, raceSummaries: List<RaceSummary>) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.spacing_3)),
    ) {
        raceSummaries.filter { it.advertisedStart.seconds - currentTime > -60 }.take(5).forEach { item ->
            item { RaceSummary(currentTime = currentTime, raceSummary = item) }
        }
    }
}

@Composable
fun RaceSummary(currentTime: Long, raceSummary: RaceSummary) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = dimensionResource(id = R.dimen.spacing_3))
            .background(color = Color.White),
    ) {
            Text(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.spacing_2))
                    .weight(1f),
                text = "R${raceSummary.raceNumber} ${raceSummary.meetingName}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.spacing_2)),
                text = timeLeft(currentTime = currentTime, time = raceSummary.advertisedStart.seconds),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
    }
}

private fun timeLeft(currentTime: Long, time: Long): String {
    val secondsLeft = time - currentTime
    val duration = Duration.ofSeconds(secondsLeft)
    val hours = duration.toHours()
    val minutes = duration.minusHours(hours).toMinutes()
    val seconds = duration.minusHours(hours).minusMinutes(minutes).seconds
    var timeString = ""
    if (hours > 0 || hours < 0) {
        timeString += " ${hours}h"
    }
    if (minutes > 0 || minutes < 0) {
        timeString += " ${minutes}m"
    }
    if (seconds > 0 || seconds < 0) {
        timeString += " ${seconds}s"
    }
    return timeString.takeIf { it.isNotEmpty() } ?: "0s"
}
