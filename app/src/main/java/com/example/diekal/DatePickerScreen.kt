@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.diekal

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerScreen() {

    val dateTime = LocalDateTime.now()

    val datePickerState = remember {
        DatePickerState(
            yearRange = (2023..2024),
            initialSelectedDateMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            initialDisplayMode = DisplayMode.Picker,
            initialDisplayedMonthMillis = null


        )
    }

    DatePicker(state = datePickerState)
}