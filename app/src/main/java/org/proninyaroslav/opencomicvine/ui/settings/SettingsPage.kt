/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.opencomicvine.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsMenuLink
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.types.preferences.PrefTheme
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.removeBottomPadding
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    viewModel: SettingsViewModel,
    isExpandedWidth: Boolean,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val changeApiKeyState by viewModel.changeApiKey.state.collectAsStateWithLifecycle()
    val dialogState = rememberDialogState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val snackbarState = LocalAppSnackbarState.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(changeApiKeyState, dialogState) {
        when (val s = changeApiKeyState) {
            is ChangeApiKeyState.Success -> dialogState.showApiKeyDialog = false
            is ChangeApiKeyState.Failed.SaveError -> {
                coroutineScope.launch {
                    when (val error = s.error) {
                        is ApiKeyRepository.SaveResult.Failed.IO -> {
                            val res = snackbarState.showSnackbar(
                                message = context.getString(
                                    R.string.save_api_key_error_template,
                                    error.exception
                                ),
                                actionLabel = context.getString(R.string.report),
                                duration = SnackbarDuration.Long,
                            )
                            if (res == SnackbarResult.ActionPerformed) {
                                viewModel.errorReport(ErrorReportInfo(error.exception))
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }

    Body(
        state = state,
        changeApiKeyState = changeApiKeyState,
        dialogState = dialogState,
        onApiKeyChanged = { viewModel.changeApiKey(it) },
        onSearchHistorySizeChanged = {
            viewModel.changeSearchHistorySize(it)
            dialogState.showSearchHistorySizeChanged = false
        },
        onThemeChanged = { viewModel.changeTheme(it) },
        isExpandedWidth = isExpandedWidth,
        onBackButtonClicked = onBackButtonClicked,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

@Stable
private class DialogState {
    var showApiKeyDialog by mutableStateOf(false)
    var showSearchHistorySizeChanged by mutableStateOf(false)
}

@Composable
private fun rememberDialogState() = remember { DialogState() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Body(
    modifier: Modifier = Modifier,
    state: SettingsState,
    changeApiKeyState: ChangeApiKeyState,
    dialogState: DialogState,
    isExpandedWidth: Boolean,
    onApiKeyChanged: (String) -> Unit,
    onSearchHistorySizeChanged: (Int) -> Unit,
    onThemeChanged: (PrefTheme) -> Unit,
    onBackButtonClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    Scaffold(
        topBar = {
            SettingsAppBar(
                onBackButtonClicked = onBackButtonClicked,
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier.then(
            scrollBehavior?.let {
                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            } ?: Modifier
        ),
    ) { contentPadding ->
        val direction = LocalLayoutDirection.current
        val newPadding = contentPadding.removeBottomPadding(direction = direction)

        val list = @Composable {
            LazyColumn(
                contentPadding = if (isExpandedWidth) {
                    PaddingValues(0.dp)
                } else {
                    newPadding
                },
                modifier = modifier.then(
                    if (isExpandedWidth) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier.fillMaxSize()
                    }
                ),
            ) {
                item {
                    Theme(
                        theme = state.theme,
                        onThemeChanged = onThemeChanged,
                    )
                }
                item {
                    ApiKey(
                        state = state,
                        changeApiKeyState = changeApiKeyState,
                        showDialog = dialogState.showApiKeyDialog,
                        onApiKeyChanged = onApiKeyChanged,
                        onShowDialog = { dialogState.showApiKeyDialog = it },
                    )
                }
                item {
                    SearchHistorySize(
                        state = state,
                        showDialog = dialogState.showSearchHistorySizeChanged,
                        onSearchHistorySizeChanged = onSearchHistorySizeChanged,
                        onShowDialog = { dialogState.showSearchHistorySizeChanged = it },
                    )
                }
            }
        }

        if (isExpandedWidth) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(newPadding),
            ) {
                val colorScheme = MaterialTheme.colorScheme
                val width = if (maxWidth >= 1024.dp) {
                    maxWidth / 2
                } else {
                    maxWidth - maxWidth / 4
                }
                val elevation = 1.dp
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceColorAtElevation(elevation),
                        contentColor = colorScheme.onSurface,
                    ),
                    modifier = Modifier
                        .width(width)
                        .heightIn(min = width)
                        .align(Alignment.Center)
                ) {
                    CompositionLocalProvider(LocalAbsoluteTonalElevation provides elevation) {
                        list()
                    }
                }
            }
        } else {
            list()
        }
    }
}

@Composable
private fun Theme(
    theme: PrefTheme,
    onThemeChanged: (PrefTheme) -> Unit,
) {
    SettingsList(
        state = rememberSettingsState(
            value = theme.id,
            defaultValue = PrefTheme.Unknown.id,
            onValueChanged = { onThemeChanged(PrefTheme.fromId(it)) },
        ),
        icon = {
            Icon(
                painterResource(R.drawable.ic_palette_24),
                contentDescription = null,
            )
        },
        title = { Text(stringResource(R.string.pref_theme_title)) },
        items = stringArrayResource(R.array.pref_theme_items).toList(),
        onItemSelected = { index, _ ->
            onThemeChanged(PrefTheme.fromId(index))
        }
    )
}

@Composable
private fun ApiKey(
    state: SettingsState,
    changeApiKeyState: ChangeApiKeyState,
    showDialog: Boolean,
    onApiKeyChanged: (String) -> Unit,
    onShowDialog: (Boolean) -> Unit,
) {
    SettingsMenuLink(
        icon = {
            Icon(
                painterResource(R.drawable.ic_key_24),
                contentDescription = null,
            )
        },
        title = { Text(stringResource(R.string.pref_api_key_title)) },
        onClick = { onShowDialog(true) },
    )

    if (showDialog) {
        ApiKeyDialog(
            state = state,
            changeApiKeyState = changeApiKeyState,
            onSubmit = onApiKeyChanged,
            onDismissRequest = { onShowDialog(false) },
        )
    }
}

@Composable
fun ApiKeyDialog(
    state: SettingsState,
    changeApiKeyState: ChangeApiKeyState,
    onSubmit: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var apiKey by remember(state) { mutableStateOf(state.apiKey ?: "") }

    AlertDialog(
        title = {
            Text(stringResource(R.string.pref_api_key_title))
        },
        confirmButton = {
            TextButton(
                onClick = { onSubmit(apiKey) },
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        onDismissRequest = onDismissRequest,
        text = {
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                isError = changeApiKeyState is ChangeApiKeyState.Failed,
                supportingText = when (changeApiKeyState) {
                    is ChangeApiKeyState.Failed.EmptyKey -> {
                        { Text(stringResource(R.string.comic_vine_api_key_empty_error)) }
                    }

                    else -> null
                },
            )
        }
    )
}

@Composable
private fun SearchHistorySize(
    state: SettingsState,
    showDialog: Boolean,
    onSearchHistorySizeChanged: (Int) -> Unit,
    onShowDialog: (Boolean) -> Unit,
) {
    SettingsMenuLink(
        icon = {
            Icon(
                painterResource(R.drawable.ic_history_24),
                contentDescription = null,
            )
        },
        title = { Text(stringResource(R.string.pref_search_history_size_title)) },
        subtitle = { Text("${state.searchHistorySize}") },
        onClick = { onShowDialog(true) },
    )

    if (showDialog) {
        SearchHistorySizeDialog(
            state = state,
            onSubmit = onSearchHistorySizeChanged,
            onDismissRequest = { onShowDialog(false) },
        )
    }
}

@Composable
fun SearchHistorySizeDialog(
    state: SettingsState,
    onSubmit: (Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val digitsPattern = remember { Regex("^\\d+\$") }
    var searchHistorySize by remember(state) { mutableStateOf("${state.searchHistorySize}") }

    AlertDialog(
        title = {
            Text(stringResource(R.string.pref_search_history_size_title))
        },
        confirmButton = {
            TextButton(
                onClick = { onSubmit(searchHistorySize.toIntOrNull() ?: 0) },
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        onDismissRequest = onDismissRequest,
        text = {
            OutlinedTextField(
                value = searchHistorySize,
                onValueChange = {
                    if (it.isEmpty() || digitsPattern.matches(it)) {
                        searchHistorySize = it
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewSettingsPage() {
    OpenComicVineTheme {
        Body(
            state = SettingsState.Loaded(
                apiKey = "123",
                searchHistorySize = 10,
                theme = PrefTheme.System,
            ),
            changeApiKeyState = ChangeApiKeyState.Initial,
            dialogState = rememberDialogState(),
            onApiKeyChanged = {},
            onSearchHistorySizeChanged = {},
            onThemeChanged = {},
            isExpandedWidth = false,
            onBackButtonClicked = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Expanded width",
    device = "spec:width=1280dp,height=800dp,dpi=480"
)
@Composable
private fun PreviewSettingsPage_ExpandedWidth() {
    OpenComicVineTheme {
        Body(
            state = SettingsState.Loaded(
                apiKey = "123",
                searchHistorySize = 10,
                theme = PrefTheme.System,
            ),
            changeApiKeyState = ChangeApiKeyState.Initial,
            dialogState = rememberDialogState(),
            onApiKeyChanged = {},
            onSearchHistorySizeChanged = {},
            onThemeChanged = {},
            isExpandedWidth = true,
            onBackButtonClicked = {},
        )
    }
}
