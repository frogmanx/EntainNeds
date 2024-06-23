package com.example.entainneds

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.entainneds.backend.RaceSummary
import com.example.entainneds.backend.Time
import com.example.entainneds.ui.RaceSummaryModel
import com.example.entainneds.ui.RaceSummaryView

import org.junit.Test

import org.junit.Rule

class RaceSummaryViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val summary = RaceSummary(
        raceId = "Test",
        raceNumber = 1,
        meetingName = "Richmond",
        categoryId = "Test",
        advertisedStart = Time(3000)
    )

    @Test
    fun whenArticleIsLoading_loadingIndicatorIsDisplayed() {

        composeTestRule.setContent {
            RaceSummaryView(raceSummaryModel = RaceSummaryModel(loading = true), currentTimeSec = 3000, onFilterItemSelected = {})
        }

        composeTestRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Error").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("RaceSummaryList").assertIsNotDisplayed()
    }

    @Test
    fun whenArticleIsNotLoading_loadingIndicatorIsNotDisplayed() {

        composeTestRule.setContent {
            RaceSummaryView(raceSummaryModel = RaceSummaryModel(loading = false), currentTimeSec = 3000, onFilterItemSelected = {})
        }

        composeTestRule.onNodeWithTag("LoadingIndicator").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("Error").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("RaceSummaryList").assertIsNotDisplayed()
    }

    @Test
    fun whenArticleHasErrored_errorIsDisplayed() {

        composeTestRule.setContent {
            RaceSummaryView(raceSummaryModel = RaceSummaryModel(loading = false, error = "Error"), currentTimeSec = 3000, onFilterItemSelected = {})
        }

        composeTestRule.onNodeWithTag("LoadingIndicator").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("RaceSummaryList").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("Error").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Error").assertTextEquals("Error")
    }

    @Test
    fun whenArticlesNotEmpty_showArticleList() {

        composeTestRule.setContent {
            RaceSummaryView(raceSummaryModel = RaceSummaryModel(loading = false, raceSummaries = listOf(summary)), currentTimeSec = 3000, onFilterItemSelected = {})
        }

        composeTestRule.onNodeWithTag("LoadingIndicator").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("RaceSummaryList").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Error").assertIsNotDisplayed()
    }
}