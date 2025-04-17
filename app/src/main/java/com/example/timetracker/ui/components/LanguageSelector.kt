package com.example.timetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timetracker.R
import com.example.timetracker.viewmodel.WorkViewModel

@Composable
fun LanguageSelector(viewModel: WorkViewModel = viewModel()) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val languageOptions = mapOf(
        "en" to stringResource(R.string.language_english),
        "nl" to stringResource(R.string.language_dutch),
        "uk" to stringResource(R.string.language_ukrainian)
    )

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Text(
            text = stringResource(R.string.choose_language),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = languageOptions[selectedLanguage] ?: selectedLanguage,
                onValueChange = {},
                label = { Text(stringResource(R.string.choose_language)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languageOptions.forEach { (code, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            viewModel.updateLanguage(code)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
 