package com.adam.abcnews.backend

import com.example.entainneds.backend.RaceSummary
import kotlinx.coroutines.flow.Flow

interface NedsServiceHelper {
    fun getNextRaceSummaries(): Flow<List<RaceSummary>>
}