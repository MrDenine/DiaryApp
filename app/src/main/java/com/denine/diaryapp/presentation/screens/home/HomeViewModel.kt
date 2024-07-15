package com.denine.diaryapp.presentation.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denine.diaryapp.data.repository.Diaries
import com.denine.diaryapp.data.repository.MongoDB
import com.denine.diaryapp.model.RequestState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.debounce

class HomeViewModel: ViewModel() {

    private lateinit var allDiariesJob: Job
    private lateinit var filteredDiariesJob: Job

    var diaries: MutableState<Diaries> = mutableStateOf(RequestState.Idle)

    init {
        observeAllDiaries()
    }

    @OptIn(FlowPreview::class)
    private fun observeAllDiaries(){
        allDiariesJob = viewModelScope.launch {
            if (::filteredDiariesJob.isInitialized) {
                filteredDiariesJob.cancelAndJoin()
            }
            MongoDB.getAllDiaries().collect { result ->
                diaries.value = result
            }
        }

    }

}