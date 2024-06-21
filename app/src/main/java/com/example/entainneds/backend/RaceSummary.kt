package com.example.entainneds.backend


data class NedsResponse(
    val nextToGoIds: List<String>,
    val raceSummaries: Map<String, RaceSummary>
)

data class RaceSummary(
    val raceId: String,
    val raceName: String,
    val raceNumber: Int,
    val meetingId: String,
    val meetingName: String,
    val categoryId: String,
    val advertisedStart: Time,
    val raceForm: Form,
    val venueId: String,
)

data class Time(
    val seconds: Double,
)

data class Form(
    val distance: Int,
    val distanceType: DistanceType,
    val distanceTypeId: String,
    val trackCondition: Condition,
    val trackConditionId: String,
)

data class DistanceType(
    val id: String,
    val name: String,
    val shortName: String,
)

data class Condition(
    val id: String,
    val name: String,
    val shortName: String,
)
