package com.example.entainneds.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.entainneds.backend.RaceSummary
import com.example.entainneds.backend.Time

val summary = RaceSummary(
    raceId = "Test",
    raceNumber = 1,
    meetingName = "Richmond",
    categoryId = "Test",
    advertisedStart = Time(3000)
)

@Preview
@Composable
fun RaceSummaryView_loadingWithNoData() {
    RaceSummaryView(
        raceSummaryModel = RaceSummaryModel(loading = true),
        currentTimeSec = 2920,
        onFilterItemSelected = {}
    )
}

@Preview
@Composable
fun RaceSummaryView_withData() {
    RaceSummaryView(
        raceSummaryModel = RaceSummaryModel(
            raceSummaries = listOf(summary, summary, summary, summary, summary, summary, summary)
        ),
        currentTimeSec = 2920,
        onFilterItemSelected = {}
    )
}

@Preview
@Composable
fun RaceSummaryView_withData_filtered() {
    RaceSummaryView(
        raceSummaryModel = RaceSummaryModel(
            raceSummaries = listOf(summary, summary, summary, summary, summary, summary, summary),
            filteredItems = setOf("Test"),
        ),
        currentTimeSec = 2920,
        onFilterItemSelected = {}
    )
}

@Preview
@Composable
fun RaceSummaryView_withError() {
    RaceSummaryView(
        raceSummaryModel = RaceSummaryModel(error = "Error", loading = false),
        currentTimeSec = 2920,
        onFilterItemSelected = {}
    )
}
