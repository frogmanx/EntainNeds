package com.example.entainneds.backend

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class RaceSummaryRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private val raceSummary: RaceSummary = mockk()

    private val abcResponse: NedsResponse = mockk {
        every { data } returns mockk {
            every { nextToGoIds } returns listOf("Test")
            every { raceSummaries } returns mapOf("Test" to raceSummary)
        }
    }

    private val nedsService: NedsService = mockk {
        coEvery { getRaces(any(), any()) } returns abcResponse
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun fetchArticles_withNormalResponse() = runTest(UnconfinedTestDispatcher()) {
        val raceSummaryRepository = RaceSummaryRepository(nedsService)
        raceSummaryRepository.getNextRaceSummaries().first() shouldBe listOf(raceSummary)
    }
}