package com.denine.diaryapp.presentation.screens.write

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denine.diaryapp.model.Diary
import com.denine.diaryapp.model.GalleryState
import com.denine.diaryapp.model.Mood
import java.time.ZonedDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WriteScreen(
    uiState: UiState,
    moodName:() -> String,
    pagerState:PagerState,
    galleryState: GalleryState,
    onTitleChange:(String) -> Unit,
    onDescriptionChange:(String) -> Unit,
    onDeletedConfirm: ()-> Unit,
    onBackPressed: () -> Unit,
    onSaveClicked: (Diary) -> Unit,
    onDateTimeUpdated: (ZonedDateTime) -> Unit,
    onImageSelect: (Uri) -> Unit

){
    LaunchedEffect (key1 = uiState.mood) {
        pagerState.scrollToPage(Mood.valueOf(uiState.mood.name).ordinal)
    }
    Scaffold(
        topBar = {
            WriteTopBar(
                onBackPressed = onBackPressed,
                onDeleteConfirmed = onDeletedConfirm,
                selectedDiary = uiState.selectedDiary,
                moodName = moodName,
                onDateTimeUpdated = onDateTimeUpdated
            )
        },
        content = {
            WriteContent(
                pagerState = pagerState,
                galleryState = galleryState,
                uiState = uiState,
                title = uiState.title,
                onTitleChanged = onTitleChange,
                description = uiState.description,
                onDescriptionChanged = onDescriptionChange,
                paddingValues = it,
                onSaveClicked = onSaveClicked,
                onImageSelect = onImageSelect
            )
        }
    )
}