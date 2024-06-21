package com.example.entainneds.backend

import com.adam.abcnews.backend.NedsServiceHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RaceSummaryRepository @Inject constructor(private val nedsService: NedsService) :
    NedsServiceHelper {

    override fun getNextRaceSummaries(): Flow<List<RaceSummary>>  = flow {
        val response = nedsService.getRaces(method = METHOD, count = COUNT).data
        val list: MutableList<RaceSummary> = mutableListOf()
        response.nextToGoIds.forEach {
            response.raceSummaries[it]?.let { summary ->
                list.add(summary)
            }
        }
        emit(list.toList())
    }

    companion object {
        private const val METHOD = "nextraces"
        private const val COUNT = 10
    }
}
