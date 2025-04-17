package com.example.timetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    initialTime: LocalTime = LocalTime.now()
) {
    var selectedTime by remember { mutableStateOf(initialTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        text = {
            TimePicker(
                state = rememberTimePickerState(
                    initialHour = initialTime.hour,
                    initialMinute = initialTime.minute
                ),
                onTimeChange = { hour, minute ->
                    selectedTime = LocalTime.of(hour, minute)
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(selectedTime)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 