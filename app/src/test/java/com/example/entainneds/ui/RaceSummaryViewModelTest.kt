package com.example.entainneds.ui

import com.example.entainneds.backend.RaceSummary
import com.example.entainneds.backend.RaceSummaryRepository
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class RaceSummaryViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val flow = MutableSharedFlow<List<RaceSummary>>()

    private val raceSummary: RaceSummary = mockk()

    private val raceSummaryRepository: RaceSummaryRepository = mockk {
        every { getNextRaceSummaries() } returns flow
    }

    @Test
    fun fetchArticles_withNormalResponse() = runTest(UnconfinedTestDispatcher()) {
        val raceSummaryViewModel = RaceSummaryViewModel(raceSummaryRepository)
        val list: List<RaceSummary> = listOf(raceSummary)
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            raceSummaryViewModel.uiState.collect {}
        }
        raceSummaryViewModel.fetchRaceSummaries()
        val raceSummaryModelPreFetch = raceSummaryViewModel.uiState.value
        raceSummaryModelPreFetch.loading shouldBe true
        raceSummaryModelPreFetch.raceSummaries shouldBe listOf()
        raceSummaryModelPreFetch.error shouldBe null
        flow.emit(list)
        val raceSummaryPostFetch = raceSummaryViewModel.uiState.value
        raceSummaryPostFetch.raceSummaries shouldBe list
        raceSummaryPostFetch.loading shouldBe false
        raceSummaryPostFetch.error shouldBe null
        collectJob.cancel()
    }

    @ExperimentalCoroutinesApi
    class MainDispatcherRule(
        private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
    ) : TestWatcher() {

        override fun starting(description: Description) {
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }
}