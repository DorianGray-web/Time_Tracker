package com.example.timetracker.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.timetracker.data.model.WorkEntry
import com.example.timetracker.ui.components.DatePickerDialog
import com.example.timetracker.ui.components.TimePickerDialog
import com.example.timetracker.util.PermissionHelper
import com.example.timetracker.viewmodel.WorkViewModel
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkEntryFormScreen(
    viewModel: WorkViewModel,
    entry: WorkEntry? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // State for form fields
    var date by remember { mutableStateOf(entry?.date ?: LocalDate.now()) }
    var startTime by remember { mutableStateOf(entry?.startTime ?: LocalTime.now()) }
    var endTime by remember { mutableStateOf(entry?.endTime ?: LocalTime.now()) }
    var commentEn by remember { mutableStateOf(entry?.commentEn ?: "") }
    var commentNl by remember { mutableStateOf(entry?.commentNl ?: "") }
    var photoPath by remember { mutableStateOf(entry?.photoPath) }
    
    // Dialog states
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    
    // Photo capture
    val photoFile = remember { File(context.filesDir, "photo_${System.currentTimeMillis()}.jpg") }
    val photoUri = remember { FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile) }
    
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoPath = photoFile.absolutePath
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entry == null) "Add Work Entry" else "Edit Work Entry") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Picker
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Date: ${date.toString()}")
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismiss = { showDatePicker = false },
                    onDateSelected = { selectedDate ->
                        date = selectedDate
                    },
                    initialDate = date
                )
            }

            // Time Pickers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { showStartTimePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Start: ${startTime.toString()}")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { showEndTimePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("End: ${endTime.toString()}")
                }
            }

            if (showStartTimePicker) {
                TimePickerDialog(
                    onDismiss = { showStartTimePicker = false },
                    onTimeSelected = { selectedTime ->
                        startTime = selectedTime
                    },
                    initialTime = startTime
                )
            }

            if (showEndTimePicker) {
                TimePickerDialog(
                    onDismiss = { showEndTimePicker = false },
                    onTimeSelected = { selectedTime ->
                        endTime = selectedTime
                    },
                    initialTime = endTime
                )
            }

            // Comments
            OutlinedTextField(
                value = commentEn,
                onValueChange = { commentEn = it },
                label = { Text("Comment (EN)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            OutlinedTextField(
                value = commentNl,
                onValueChange = { commentNl = it },
                label = { Text("Comment (NL)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            // Photo Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Photo Preview
                if (photoPath != null) {
                    Image(
                        painter = rememberAsyncImagePainter(File(photoPath)),
                        contentDescription = "Work Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (PermissionHelper.checkAndRequestCameraPermission(context)) {
                                takePhotoLauncher.launch(photoUri)
                            }
                        }
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Take Photo")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Take Photo")
                    }

                    if (photoPath != null) {
                        IconButton(onClick = { photoPath = null }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove Photo")
                        }
                    }
                }
            }

            // Save Button
            Button(
                onClick = {
                    val newEntry = WorkEntry(
                        id = entry?.id ?: 0,
                        date = date,
                        startTime = startTime,
                        endTime = endTime,
                        commentEn = commentEn,
                        commentNl = commentNl,
                        photoPath = photoPath
                    )
                    
                    if (entry == null) {
                        viewModel.addWorkEntry(newEntry)
                    } else {
                        viewModel.updateWorkEntry(newEntry)
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (entry == null) "Add Entry" else "Save Changes")
            }
        }
    }
} 