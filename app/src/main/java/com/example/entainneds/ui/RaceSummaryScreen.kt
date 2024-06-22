package com.example.entainneds.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.entainneds.R
import com.example.entainneds.backend.RaceSummary
import com.example.entainneds.ui.theme.Purple80
import kotlinx.coroutines.delay
import java.time.Duration

@Composable
fun RaceSummaryScreen(modifier: Modifier = Modifier, raceSummaryViewModel: RaceSummaryViewModel = viewModel()) {
    val raceSummaryModel by raceSummaryViewModel.uiState.collectAsState()
    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    RaceSummaryView(modifier = modifier, raceSummaryModel = raceSummaryModel, currentTimeSec = currentTimeMs / 1000)
    LaunchedEffect(Unit) {
        delay(60 * 1000)
        raceSummaryViewModel.fetchRaceSummaries()
    }
    LifecycleEventListener {
        when (it) {
            Lifecycle.Event.ON_RESUME -> {
                currentTimeMs = System.currentTimeMillis()
            }
            else -> Unit
        }
    }
    LaunchedEffect(currentTimeMs) {
        delay(1000)
        currentTimeMs = System.currentTimeMillis()
    }
}

@Composable
fun RaceSummaryView(modifier: Modifier = Modifier, raceSummaryModel: RaceSummaryModel, currentTimeSec: Long) {
    Box(modifier = modifier.fillMaxSize()) {
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
            RaceSummaryList(modifier = Modifier.testTag("RaceSummaryList"), currentTimeSec = currentTimeSec, raceSummaries = it)
        }
        if (raceSummaryModel.loading && raceSummaryModel.raceSummaries.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag("LoadingIndicator"),
            )
        }
    }
}

@Composable
fun RaceSummaryList(currentTimeSec: Long, modifier: Modifier = Modifier, raceSummaries: List<RaceSummary>) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.spacing_3)),
    ) {
        raceSummaries.filter { it.advertisedStart.seconds - currentTimeSec > -60 }.take(5).forEach { item ->
            item { RaceSummary(currentTimeSec = currentTimeSec, raceSummary = item) }
        }
    }
}

@Composable
fun RaceSummary(currentTimeSec: Long, raceSummary: RaceSummary) {
    val spacing2 = dimensionResource(id = R.dimen.spacing_2)
    val spacing3 = dimensionResource(id = R.dimen.spacing_3)
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = spacing3)
            .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.curve)))
            .background(color = Color.White),
    ) {
            Text(
                modifier = Modifier
                    .padding(start = spacing2, top = spacing2, bottom = spacing2, end = 0.dp),
                text = "R${raceSummary.raceNumber}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier
                    .padding(spacing2)
                    .weight(1f),
                text = raceSummary.meetingName,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier
                    .padding(spacing2),
                text = timeLeft(currentTimeSec = currentTimeSec, time = raceSummary.advertisedStart.seconds),
                style = MaterialTheme.typography.bodyLarge,
            )
    }
}

@Composable
fun LifecycleEventListener(onEvent : (event: Lifecycle.Event) -> Unit) {

    val eventHandler = rememberUpdatedState(newValue = onEvent)
    val lifecycleOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value){
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            eventHandler.value(event)
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

private fun timeLeft(currentTimeSec: Long, time: Long): String {
    val secondsLeft = time - currentTimeSec
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
