package com.example.entainneds.backend


data class NedsResponse(
    val data: Data,
)

data class Data(
    val nextToGoIds: List<String>,
    val raceSummaries: Map<String, RaceSummary>
)

data class RaceSummary(
    val raceId: String,
    val raceNumber: Int,
    val meetingName: String,
    val categoryId: String,
    val advertisedStart: Time,
)

data class Time(
    val seconds: Long,
)
