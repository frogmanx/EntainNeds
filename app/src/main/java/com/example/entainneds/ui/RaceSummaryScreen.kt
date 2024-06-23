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
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.entainneds.R
import com.example.entainneds.backend.RaceSummary
import kotlinx.coroutines.delay
import java.time.Duration

@Composable
fun RaceSummaryScreen(modifier: Modifier = Modifier, raceSummaryViewModel: RaceSummaryViewModel = viewModel()) {
    val raceSummaryModel by raceSummaryViewModel.uiState.collectAsState()
    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isResumed by remember {
        mutableStateOf(true)
    }
    RaceSummaryView(modifier = modifier, raceSummaryModel = raceSummaryModel, filteredItems = raceSummaryModel.filteredItems, currentTimeSec = currentTimeMs / 1000) {
        uuid ->
        raceSummaryViewModel.updateFilteredItems(item = uuid)
    }
    LaunchedEffect(isResumed) {
        while(isResumed) {
            raceSummaryViewModel.fetchRaceSummaries()
            delay(30 * 1000)
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
fun RaceSummaryView(
    modifier: Modifier = Modifier,
    filteredItems: Set<String> = setOf(),
    raceSummaryModel: RaceSummaryModel,
    currentTimeSec: Long,
    onFilterItemSelected: (String) -> Unit,
) {
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
            )
        } ?:
        raceSummaryModel.raceSummaries.takeIf { it.isNotEmpty() }?.let {
            var showFilter by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    Row {
                        Text(
                            modifier = Modifier
                                .padding(
                                    top = dimensionResource(id = R.dimen.spacing_3),
                                    start = dimensionResource(id = R.dimen.spacing_2),
                                    end = dimensionResource(id = R.dimen.spacing_2),
                                    bottom = dimensionResource(id = R.dimen.spacing_1),
                                ),
                            text = stringResource(id = R.string.title),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        RaceFilter { showFilter = !showFilter }
                    }
                    RaceSummaryList(
                        modifier = Modifier.testTag("RaceSummaryList"),
                        currentTimeSec = currentTimeSec,
                        raceSummaries = it,
                        filteredItems = raceSummaryModel.filteredItems
                    )
                }
                FilterList(
                    filteredItems = filteredItems,
                    showDialog = showFilter,
                    onDismissRequest = { showFilter = false },
                    onFilterItemSelected = onFilterItemSelected
                )
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
            tint = MaterialTheme.colorScheme.onSurface,
            imageVector = Icons.Rounded.FilterList,
            contentDescription = stringResource(id = R.string.filter)
        )
    }
}

@Composable
fun RaceSummaryList(currentTimeSec: Long, modifier: Modifier = Modifier, raceSummaries: List<RaceSummary>, filteredItems: Set<String>) {
    val futureRaces = raceSummaries.filter { it.advertisedStart.seconds - currentTimeSec > -60 }

    val racesToDisplay = if (filteredItems.isEmpty()) {
        futureRaces.take(5)
    } else {
        futureRaces.filter { filteredItems.contains(it.categoryId) }
    }

    if (racesToDisplay.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(id = R.string.no_races),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.spacing_3)),
        ) {
            racesToDisplay.forEach {
                item {
                    RaceSummary(currentTimeSec = currentTimeSec, raceSummary = it)
                }
            }
        }
    }
}

@Composable
fun FilterList(
    showDialog: Boolean,
    filteredItems: Set<String>,
    onDismissRequest: () -> Unit,
    onFilterItemSelected: (String) -> Unit,
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismissRequest() },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .shadow(
                            elevation = dimensionResource(id = R.dimen.shadow),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.spacing_2))
                        )
                        .pointerInput(Unit) { detectTapGestures { } }
                        .width(dimensionResource(id = R.dimen.dialog_width))
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.spacing_2)))
                        .background(color = MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.TopStart
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_1))
                    ) {
                        RaceSummaryModel.filterOptions.forEach {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onFilterItemSelected(it.value) },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_1)),
                                        checked = filteredItems.contains(it.value),
                                        onCheckedChange = null,
                                    )
                                    Text(
                                        text = it.key,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
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
            .background(color = MaterialTheme.colorScheme.onSurface),
    ) {
            Text(
                modifier = Modifier
                    .padding(start = spacing2, top = spacing2, bottom = spacing2),
                text = "R${raceSummary.raceNumber}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier
                    .padding(spacing2)
                    .weight(1f),
                text = raceSummary.meetingName,
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier
                    .padding(spacing2),
                text = timeLeft(currentTimeSec = currentTimeSec, time = raceSummary.advertisedStart.seconds),
                color = MaterialTheme.colorScheme.onSecondary,
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
