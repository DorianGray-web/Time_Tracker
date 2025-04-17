package com.example.timetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timetracker.R
import com.example.timetracker.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun WorkSummaryCard(viewModel: MainViewModel = viewModel()) {
    val summary by viewModel.summary.collectAsState()
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.summary_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            summary?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.total_hours),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "%.2f".format(it.totalHours),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.overtime_hours),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "%.2f".format(it.overtimeHours),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.total_earnings),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = currencyFormat.format(it.totalEarnings),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            } ?: Text(
                text = stringResource(R.string.no_entries),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 