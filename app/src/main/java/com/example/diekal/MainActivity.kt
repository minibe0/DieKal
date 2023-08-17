@file:OptIn(ExperimentalAnimationApi::class)

package com.example.diekal

import android.os.Bundle
import android.speech.RecognitionListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import com.example.diekal.ui.theme.DieKalTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //         DieKalTheme {
            //             Surface(
            //                 modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            //             ) {
//                    DatePickerScreen()

            val voiceToTextParser by lazy {
                VoiceToTextParser(application)
            }
            var canRecord by remember {
                mutableStateOf(false)
            }
            val recordAudioLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    canRecord = isGranted
                })
            LaunchedEffect(key1 = recordAudioLauncher) {
                recordAudioLauncher.launch(/* input = */ Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION)
            }
            val state by voiceToTextParser.state.collectAsState()

            Scaffold(floatingActionButton = {
                FloatingActionButton(onClick = {
                    if (state.isSpeaking) {
                        voiceToTextParser.stopListening()
                    } else {
                        voiceToTextParser.startListening(languageCode = String())
                    }
                }) {
                    AnimatedContent(targetState = state.isSpeaking, label = "") {
                        if (state.isSpeaking) {
                            Icon(imageVector = Icons.Rounded.Done, contentDescription = "")
                        } else {
                            Icon(imageVector = Icons.Rounded.Refresh, contentDescription = "")
                        }
                    }
                }

            }) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedContent(targetState = state.isSpeaking) {
                        if (state.isSpeaking) {
                            Text(text = "말하세요")
                        } else {
                            Text(text = state.spokenText.ifEmpty {
                                "마이크를 클릭하세요"

                            })
                        }
                    }
                }

            }
            //                 }// surface
            //             }//diekal theme
        }
    }
}

