@file:OptIn(ExperimentalAnimationApi::class)

package com.example.diekal

import FirestoreManager
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
import android.Manifest
import android.graphics.Color
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Start
import androidx.compose.runtime.rememberCoroutineScope
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    //firebase 구성 변수
    private val firestoreManager = FirestoreManager()

    //db에 저장된 데이터를 목록으로 불러올 때 출력하기 위해 생성 (목록데이터를 관리할 mutableStateOf)
    private val textListState = mutableStateOf(emptyList<String>())

    // 목록 표시 여부를 관리하는 상태 변수
    private var isListVisible by mutableStateOf(false)

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //         DieKalTheme {
            //             Surface(
            //                 modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            //             ) {
//                    DatePickerScreen()

            val voiceToTextParser by remember {
                mutableStateOf(VoiceToTextParser(application))
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
                recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            val state by voiceToTextParser.state.collectAsState()

            val firebaseStorage = FirebaseStorage.getInstance() //데이터 불러오기 위해 생성
            val scope = rememberCoroutineScope()

            Scaffold(floatingActionButton = {
                FloatingActionButton(onClick = {
                    if (state.isSpeaking) {
                        voiceToTextParser.stopListening()

                    }
                    voiceToTextParser.startListening(languageCode = "ko-KR")
                    voiceToTextParser.startListening(languageCode = "en-US")
                    state
//                    else {
//                        val spokenText = state.spokenText
//                        if (spokenText.isNotEmpty()) {
//                            scope.launch {
//                                firestoreManager.saveSpokenText(spokenText)
//                            }
//                        }
//                    }
                    isListVisible = false

                }) {
                    AnimatedContent(targetState = state.isSpeaking, label = "") {
                        if (state.isSpeaking) {
                            Icon(imageVector = Icons.Rounded.MicOff, contentDescription = "")
                        } else {
                            Icon(imageVector = Icons.Rounded.Mic, contentDescription = "")
                        }
                    }
                }

            }) { padding ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedContent(targetState = state.isSpeaking, label = "") {
                        if (state.isSpeaking) {
                            Text(text = "말하세요, Say something")
                        } else {
                            Text(text = state.spokenText)
                        }
                    }
                }

            }
            //                 }// surface
            //             }//diekal theme

            // firebase 저장/취소 처리
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize().padding(15.dp, 0.dp, 15.dp, 0.dp)
            ) {
                FloatingActionButton(onClick = {
                    // 업로드 및 저장 버튼 클릭 시 처리
                    if (state.spokenText.isNotEmpty()) {
                        scope.launch {
                            firestoreManager.saveSpokenText(
                                state.spokenText
                            )
                            val savedTextList = firestoreManager.getSavedTextListFromDatabase()
                            textListState.value = savedTextList
                        }

                    }
                    // 목록을 표시하도록 상태 변수 변경
                    isListVisible = false
                }) {
                    Icon(imageVector = Icons.Rounded.Save, contentDescription = "Save")
                }

                FloatingActionButton(onClick = {
                    // 초기화(취소) 버튼 클릭 시 처리
                    voiceToTextParser.stopListening()
                    isListVisible = false
                    // clear 함수를 호출하여 초기화 시켜줌
                    voiceToTextParser.clear()

                }) {
                    Icon(imageVector = Icons.Rounded.Cancel, contentDescription = "Cancel")
                }

                // 목록보기 버튼 클릭 시 처리

                FloatingActionButton(onClick = {
                    scope.launch {
                        val savedTextList = firestoreManager.getSavedTextListFromDatabase()
                        // 상태 업데이트를 통해 데이터를 화면에 표시
                        textListState.value = savedTextList
                        // 목록을 표시하도록 상태 변수 변경
                        isListVisible = true
                    }


                }) {
                    Icon(imageVector = Icons.Rounded.List, contentDescription = "List")
                }


            }
            Spacer(modifier = Modifier.height(25.dp))


            // 목록 데이터를 화면에 표시하는 부분
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(vertical = 60.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // 텍스트 목록 출력
                if (isListVisible) {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 2.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        items(textListState.value) { text ->
                            Card(
                                modifier = Modifier.padding(15.dp, 5.dp, 15.dp, 0.dp).fillMaxWidth()
                                    .height(100.dp)

                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = text,
                                        style = TextStyle(
                                            fontSize = 13.sp, color = androidx.compose.ui.graphics.Color.Gray
                                        ),
                                        modifier = Modifier.padding(15.dp)
                                    )
                                    val spokenTextDate = runBlocking {
                                        firestoreManager.getSpokenTextDate(text)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val spokenTextDate = runBlocking {
                                            firestoreManager.getSpokenTextDate(text)
                                        } // timestamp 가져오기
                                        Text(
                                            text = spokenTextDate.toString(),
                                            fontSize = 10.sp,
                                            lineHeight = 16.sp, // timestamp의 줄 높이 조절
                                            modifier = Modifier
                                                .padding(start = 15.dp) // timestamp의 위쪽 패딩 추가
                                        )

                                        IconButton(onClick = {
                                            // 삭제 버튼 클릭 시 해당 데이터 삭제
                                            scope.launch {
                                                firestoreManager.deleteSpokenText(text)
                                                val savedTextList =
                                                    firestoreManager.getSavedTextListFromDatabase()
                                                textListState.value = savedTextList
                                            }
                                        }, content = {
                                            Icon(
                                                imageVector = Icons.Rounded.Cancel,
                                                contentDescription = "Delete"
                                            )
                                        })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


