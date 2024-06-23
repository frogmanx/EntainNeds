package com.example.entainneds.ui

import com.example.entainneds.backend.RaceSummary

data class RaceSummaryModel(
    val loading: Boolean = true,
    val raceSummaries: List<RaceSummary> = listOf(),
    val filteredItems: Set<String> = setOf(),
    val error: String? = null,
) {
    companion object {
        val filterOptions: Map<String, String> = mapOf(
            "Greyhound racing" to "0daef0d7-bf3c-4f50-921d-8e818c60fe61",
            "Harness racing" to "161d9be2-e909-4326-8c2c-35ed71fb460b",
            "Horse racing" to "4a2788f8-e825-4d36-9894-efd4baf1cfae",
        )
    }
}

