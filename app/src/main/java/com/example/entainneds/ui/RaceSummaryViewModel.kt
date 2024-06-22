package com.example.entainneds.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.entainneds.backend.RaceSummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RaceSummaryViewModel @Inject constructor(
    private val raceSummaryRepository: RaceSummaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RaceSummaryModel())
    val uiState: StateFlow<RaceSummaryModel> = _uiState.asStateFlow()

    fun fetchRaceSummaries() {
        Log.i("Test", "fetchRaceSummaries")
        viewModelScope.launch {
            _uiState.update { articleModel ->
                articleModel.copy(loading = true, error = null)
            }
            raceSummaryRepository.getNextRaceSummaries()
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    _uiState.update { raceSummaryModel ->
                        raceSummaryModel.copy(loading = false, error = e.message)
                    }
                }
                .collect {
                    _uiState.update { raceSummaryModel ->
                        raceSummaryModel.copy(loading = false, raceSummaries = it, error = null)
                    }
                }
        }
    }
}