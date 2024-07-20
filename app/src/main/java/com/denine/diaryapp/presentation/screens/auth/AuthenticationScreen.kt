package com.denine.diaryapp.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.denine.diaryapp.utils.Constants.CLIENT_ID
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle

@Composable
fun AuthenticationScreen(
    authenticated:Boolean,
    loadingState:Boolean,
    oneTapState: OneTapSignInState,
    messageBarState : MessageBarState,
    onButtonClicked: () -> Unit,
    onSuccessfulFirebaseSignIn: (String) -> Unit,
    onFailureFirebaseSignIn: (Exception) -> Unit,
    onDialogDismissed: (String) -> Unit,
    navigateToHome:()->Unit
){
    Scaffold (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        content = {
            Column(
                modifier = Modifier
                    .padding(paddingValues = it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ContentWithMessageBar(
                    messageBarState = messageBarState
                ){
                    AuthenticationContent(
                        loadingState = loadingState,
                        onButtonClicked = onButtonClicked
                    )
                }
            }

        }
    )

    OneTapSignInWithGoogle(
        state = oneTapState,
        clientId = CLIENT_ID,
        onTokenIdReceived = {
            tokenId ->
            val credential = GoogleAuthProvider.getCredential(tokenId,null)
            Firebase.auth.signInWithCredential(credential)
                .addOnCompleteListener {  task ->
                    if(task.isSuccessful){
                        onSuccessfulFirebaseSignIn(tokenId)
                    } else {
                        task.exception?.let { it -> onFailureFirebaseSignIn(it) }
                    }
                }
        },
        onDialogDismissed = {
            message ->
            onDialogDismissed(message)
        },
    )

    LaunchedEffect(key1 = authenticated) {
        if(authenticated){
            navigateToHome()
        }
    }
}