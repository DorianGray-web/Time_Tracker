package com.example.timetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.timetracker.R
import com.example.timetracker.utils.UiMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSnackbar(
    message: UiMessage?,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (message != null) {
        Snackbar(
            modifier = modifier.padding(16.dp),
            action = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onRetry != null) {
                        TextButton(
                            onClick = onRetry,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.retry)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.retry))
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.dismiss)
                        )
                    }
                }
            },
            containerColor = when (message) {
                is UiMessage.Error -> MaterialTheme.colorScheme.errorContainer
                is UiMessage.Success -> MaterialTheme.colorScheme.primaryContainer
                is UiMessage.Info -> MaterialTheme.colorScheme.secondaryContainer
            },
            contentColor = when (message) {
                is UiMessage.Error -> MaterialTheme.colorScheme.onErrorContainer
                is UiMessage.Success -> MaterialTheme.colorScheme.onPrimaryContainer
                is UiMessage.Info -> MaterialTheme.colorScheme.onSecondaryContainer
            }
        ) {
            Text(
                text = message.message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 