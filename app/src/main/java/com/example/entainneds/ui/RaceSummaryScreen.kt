package com.example.entainneds.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
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
    var isResumed by remember {
        mutableStateOf(true)
    }
    RaceSummaryView(modifier = modifier, raceSummaryModel = raceSummaryModel, currentTimeSec = currentTimeMs / 1000)
    LaunchedEffect(isResumed) {
        while(isResumed) {
            raceSummaryViewModel.fetchRaceSummaries()
            delay(10 * 1000)
        }
    }
    LifecycleEventListener {
        when (it) {
            Lifecycle.Event.ON_RESUME -> {
                currentTimeMs = System.currentTimeMillis()
                isResumed = true
            }
            Lifecycle.Event.ON_PAUSE -> {
                isResumed = false
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
            var showFilter by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    RaceFilter { showFilter = !showFilter }
                    RaceSummaryList(modifier = Modifier.testTag("RaceSummaryList"), currentTimeSec = currentTimeSec, raceSummaries = it)
                }
                FilterList(
                    showDialog = showFilter,
                    onDismissRequest = { showFilter = false },
                ) {
                    Text(
                        text = "Text"
                    )
                }
            }
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
fun RaceFilter(onClick: () -> Unit) {
    val spacing2 = dimensionResource(id = R.dimen.spacing_2)
    val spacing3 = dimensionResource(id = R.dimen.spacing_3)
    Box(modifier = Modifier.fillMaxWidth()) {
        Icon(
            modifier = Modifier
                .padding(top = spacing3, start = spacing2, end = spacing2)
                .clickable { onClick() }
                .align(Alignment.TopEnd),
            tint = Color.White,
            imageVector = Icons.Sharp.Settings,
            contentDescription = stringResource(id = R.string.settings)
        )
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
fun FilterList(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .pointerInput(Unit) { detectTapGestures { } }
                        .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                        .width(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            MaterialTheme.colorScheme.surface,
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    LazyColumn {
                        item {
                            Text(text = "Greyhound racing")
                        }
                        item {
                            Text(text = "Harness racing")
                        }
                        item {
                            Text(text = "Horse racing")
                        }
                    }
                }

            }
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
                    .padding(start = spacing2, top = spacing2, bottom = spacing2),
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

    DisposableEffect(lifecycleOwner.value) {
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
