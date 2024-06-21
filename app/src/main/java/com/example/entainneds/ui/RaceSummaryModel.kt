package com.example.entainneds.ui

import com.example.entainneds.backend.RaceSummary

data class RaceSummaryModel(
    val loading: Boolean = true,
    val raceSummaries: List<RaceSummary> = listOf(),
    val error: String? = null,
)
