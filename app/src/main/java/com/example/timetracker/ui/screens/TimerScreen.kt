package com.example.timetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timetracker.viewmodel.TimerViewModel
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = hiltViewModel()
) {
    val timeEntries by viewModel.timeEntries.collectAsState()
    val currentTimer by viewModel.currentTimer.collectAsState()
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Time Tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.startTimer(description) },
            enabled = currentTimer == null
        ) {
            Text("Start Timer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentTimer != null) {
            Button(
                onClick = { currentTimer?.id?.let { viewModel.stopTimer(it) } }
            ) {
                Text("Stop Timer")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn {
            items(timeEntries) { entry ->
                TimeEntryItem(entry)
            }
        }
    }
}

@Composable
fun TimeEntryItem(entry: TimeEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = entry.description,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Start: ${entry.startTime}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (entry.endTime != null) {
                Text(
                    text = "End: ${entry.endTime}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 