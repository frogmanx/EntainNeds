package com.example.entainneds.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.entainneds.R
import com.example.entainneds.backend.RaceSummary
import com.example.entainneds.ui.theme.Purple80

@Composable
fun RaceSummaryScreen(modifier: Modifier = Modifier, raceSummaryViewModel: RaceSummaryViewModel = viewModel()) {
    val raceSummaryModel by raceSummaryViewModel.uiState.collectAsState()
    RaceSummaryView(modifier = modifier, raceSummaryModel = raceSummaryModel)
    LifecycleEventListener {
        when (it) {
            Lifecycle.Event.ON_RESUME -> {
                raceSummaryViewModel.fetchRaceSummaries()
            }
            else -> Unit
        }
    }
}

@Composable
fun RaceSummaryView(modifier: Modifier = Modifier, raceSummaryModel: RaceSummaryModel) {
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
            RaceSummaryList(modifier = Modifier.testTag("RaceSummaryList"), raceSummaries = it)
        }
        if (raceSummaryModel.loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center).testTag("LoadingIndicator"),
            )
        }
    }
}

@Composable
fun RaceSummaryList(modifier: Modifier = Modifier, raceSummaries: List<RaceSummary>) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.spacing_3)),
    ) {
        raceSummaries.forEach { item ->
            item { RaceSummary(item) }
        }
    }
}

@Composable
fun RaceSummary(raceSummary: RaceSummary) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = dimensionResource(id = R.dimen.spacing_3))
            .background(color = Color.White),
    ) {
            Text(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_2)),
                text = raceSummary.meetingName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
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
