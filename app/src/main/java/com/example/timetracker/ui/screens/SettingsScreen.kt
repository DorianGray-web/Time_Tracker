package com.example.timetracker.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timetracker.R
import com.example.timetracker.ui.components.LanguageSelector
import com.example.timetracker.ui.components.SettingsItem
import com.example.timetracker.viewmodel.SettingsViewModel
import com.example.timetracker.viewmodel.ThemeMode

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    val themeMode by viewModel.themeMode.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigation_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Selection
            SettingsItem(
                icon = Icons.Default.Brightness4,
                title = stringResource(R.string.theme),
                subtitle = when (themeMode) {
                    ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                    ThemeMode.DARK -> stringResource(R.string.theme_dark)
                    ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                },
                onClick = { showThemeDialog = true },
                contentDescription = stringResource(R.string.cd_theme_selector)
            )

            // Language Selection
            SettingsItem(
                icon = Icons.Default.Language,
                title = stringResource(R.string.language),
                subtitle = selectedLanguage,
                onClick = { /* Language selection handled by LanguageSelector */ },
                contentDescription = stringResource(R.string.cd_language_selector)
            )

            LanguageSelector(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { language ->
                    viewModel.updateLanguage(language)
                }
            )
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(stringResource(R.string.select_theme)) },
            text = {
                Column {
                    RadioButtonItem(
                        text = stringResource(R.string.theme_light),
                        selected = themeMode == ThemeMode.LIGHT,
                        onClick = { viewModel.updateThemeMode(ThemeMode.LIGHT) }
                    )
                    RadioButtonItem(
                        text = stringResource(R.string.theme_dark),
                        selected = themeMode == ThemeMode.DARK,
                        onClick = { viewModel.updateThemeMode(ThemeMode.DARK) }
                    )
                    RadioButtonItem(
                        text = stringResource(R.string.theme_system),
                        selected = themeMode == ThemeMode.SYSTEM,
                        onClick = { viewModel.updateThemeMode(ThemeMode.SYSTEM) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(stringResource(R.string.button_ok))
                }
            }
        )
    }
}

@Composable
private fun RadioButtonItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text)
    }
} 