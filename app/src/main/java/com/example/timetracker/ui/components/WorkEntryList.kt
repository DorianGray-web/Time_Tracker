package com.example.timetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timetracker.domain.model.WorkEntry
import com.example.timetracker.viewmodel.WorkViewModel
import java.time.format.DateTimeFormatter

@Composable
fun WorkEntryList(
    onEditClick: (WorkEntry) -> Unit,
    viewModel: WorkViewModel = hiltViewModel()
) {
    val workEntries by viewModel.workEntries.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = workEntries,
            key = { it.id }
        ) { entry ->
            WorkEntryCard(
                entry = entry,
                selectedLanguage = selectedLanguage,
                onEditClick = { onEditClick(entry) },
                onDeleteClick = { viewModel.deleteWorkEntry(entry) }
            )
        }
    }
}

@Composable
fun WorkEntryCard(
    entry: WorkEntry,
    selectedLanguage: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${entry.startTime.format(DateTimeFormatter.ISO_LOCAL_TIME)} - " +
                            entry.endTime.format(DateTimeFormatter.ISO_LOCAL_TIME),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (selectedLanguage == "en") entry.commentEn else entry.commentNl,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
} 