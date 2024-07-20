package com.denine.diaryapp.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.denine.diaryapp.model.Diary
import com.denine.diaryapp.model.GalleryImage
import com.denine.diaryapp.model.Mood
import com.denine.diaryapp.model.RequestState
import com.denine.diaryapp.model.rememberGalleryState
import com.denine.diaryapp.presentation.components.DisplayAlertDialog
import com.denine.diaryapp.presentation.screens.auth.AuthenticationScreen
import com.denine.diaryapp.presentation.screens.auth.AuthenticationViewModel
import com.denine.diaryapp.presentation.screens.home.HomeScreen
import com.denine.diaryapp.presentation.screens.home.HomeViewModel
import com.denine.diaryapp.presentation.screens.write.WriteScreen
import com.denine.diaryapp.presentation.screens.write.WriteViewModel
import com.denine.diaryapp.utils.Constants.APP_ID
import com.denine.diaryapp.utils.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController,
    onDataLoaded: () -> Unit
){
    NavHost(
        startDestination = startDestination ,
        navController = navController
    ){
        authenticationRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            },
            onDataLoaded = onDataLoaded
        )
        homeRoute(
            navigateToWrite = {
                navController.navigate(Screen.Write.route)
            },
            navigateToWriteWithArgs = {
                navController.navigate(Screen.Write.passDiaryId(diaryId = it))
            },
            navigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            },
            onDataLoaded = onDataLoaded,

        )
        writeRoute(
            navigateBack = {
                navController.popBackStack()
            }
        )
    }

}

fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit,
    onDataLoaded: () -> Unit
){
    composable(route = Screen.Authentication.route){
        val viewModel : AuthenticationViewModel = viewModel()
        val authenticated by viewModel.authenticated
        val loadingState by viewModel.loadingState
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        LaunchedEffect (key1 = Unit) {
            onDataLoaded()
        }

        AuthenticationScreen(
            authenticated = authenticated,
            oneTapState = oneTapState,
            loadingState = loadingState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onSuccessfulFirebaseSignIn = {
                tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully Authenticated!")
                        viewModel.setLoading(false)
                    },
                    onError = {
                        messageBarState.addError(Exception(it))
                        viewModel.setLoading(false)
                    }
                )
            },
            onFailureFirebaseSignIn = {
                messageBarState.addError(Exception(it))
                viewModel.setLoading(false)
            },
            onDialogDismissed = {
                message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            navigateToHome = navigateToHome

        )
    }
}

fun NavGraphBuilder.homeRoute(
    navigateToWriteWithArgs: (String) -> Unit,
    navigateToWrite: () -> Unit,
    navigateToAuth: () -> Unit,
    onDataLoaded: () -> Unit
){
    composable(route = Screen.Home.route){
        val viewModel: HomeViewModel = viewModel()
        val diaries by viewModel.diaries
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var signOutDialogOpened by remember {
            mutableStateOf(false)
        }
        val scope = rememberCoroutineScope()

        LaunchedEffect (key1 = diaries) {
            if(diaries !is RequestState.Loading){
                onDataLoaded()
            }
        }

        HomeScreen(
            diaries = diaries,
            drawerState = drawerState,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            onSignOutClicked = {
                signOutDialogOpened = true
            },
            navigateToWrite = navigateToWrite,
            navigateToWriteWithArgs = navigateToWriteWithArgs
        )

        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure yow want to Sign Out form your google Account?",
            dialogOpened = signOutDialogOpened,
            onDialogClosed = { signOutDialogOpened = false },
            onYesClicked = {
                scope.launch (Dispatchers.IO) {
                    val user = App.create(APP_ID).currentUser
                    if(user != null){
                        user.logOut()
                        withContext(Dispatchers.Main){
                            navigateToAuth()
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.writeRoute(navigateBack: () -> Unit) {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = WRITE_SCREEN_ARGUMENT_KEY){
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ){
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val viewModel : WriteViewModel = hiltViewModel()
        val galleryState = viewModel.galleryState
        val uiState = viewModel.uiState
        val pagerState = rememberPagerState(pageCount = {Mood.entries.size})
        val pageNumber by remember {
            derivedStateOf { pagerState.currentPage }
        }

        LaunchedEffect (key1 = uiState) {
            Log.d("SelectedDiary","${uiState.selectedDiaryId}")
        }

        WriteScreen(
            moodName = { Mood.entries[pageNumber].name},
            onTitleChange = {
                viewModel.setTitle(title = it)
            },
            onDescriptionChange = {
                viewModel.setDescription(description = it)
            },
            onDateTimeUpdated = {
                viewModel.updateDateTime(zonedDateTime = it)
            },
            onDeletedConfirm = {
                viewModel.deleteDiary(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateBack()
                    },
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            onImageSelect = {
                val type = context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg"
                viewModel.addImage(
                    image = it,
                    imageType = type
                )
            },
            onBackPressed = navigateBack,
            pagerState = pagerState,
            galleryState = galleryState,
            uiState = uiState,
            onSaveClicked = {
                scope.launch (Dispatchers.IO) {
                    viewModel.upsertDiary(
                        diary = it.apply { mood = Mood.entries[pageNumber].name},
                        onSuccess = {navigateBack()} ,
                        onError = {}
                    )
                }
            },

        )
    }
}