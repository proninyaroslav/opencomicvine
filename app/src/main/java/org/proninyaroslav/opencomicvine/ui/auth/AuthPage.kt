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

package org.proninyaroslav.opencomicvine.ui.auth

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.types.preferences.PrefTheme
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.addExtraPadding
import org.proninyaroslav.opencomicvine.ui.components.*
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import org.proninyaroslav.opencomicvine.ui.theme.ThemeViewModel
import java.io.IOException

@Composable
fun AuthPage(
    viewModel: AuthViewModel,
    themeViewModel: ThemeViewModel,
    isExpandedWidth: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val submitState by viewModel.submit.state.collectAsStateWithLifecycle()
    val themeState by themeViewModel.theme.collectAsStateWithLifecycle()
    val currentOnClose by rememberUpdatedState(onClose)

    LaunchedEffect(submitState) {
        if (submitState == AuthSubmitState.Submitted) {
            currentOnClose()
        }
    }

    OpenComicVineTheme(
        darkTheme = when (themeState) {
            PrefTheme.Dark -> true
            PrefTheme.Light -> false
            PrefTheme.System,
            PrefTheme.Unknown -> isSystemInDarkTheme()
        },
    ) {
        Scaffold(
            snackbarHost = {
                AppSnackbarHost(
                    hostState = LocalAppSnackbarState.current,
                    isExpandedWidth = isExpandedWidth,
                )
            },
            modifier = modifier,
        ) { contentPadding ->
            Body(
                contentPadding = contentPadding,
                state = state,
                submitState = submitState,
                isExpandedWidth = isExpandedWidth,
                apiKeyChanged = { viewModel.changeApiKey(it) },
                onSubmit = { viewModel.submit() },
                onReport = viewModel::errorReport,
            )
        }
    }

}

@Composable
private fun Body(
    contentPadding: PaddingValues,
    state: AuthState,
    submitState: AuthSubmitState,
    isExpandedWidth: Boolean,
    apiKeyChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val newPadding = contentPadding.addExtraPadding(
        direction = LocalLayoutDirection.current,
        extraPadding = PaddingValues(16.dp),
    )
    val list = @Composable {
        BoxWithConstraints {
            val width = if (isExpandedWidth) max(maxWidth / 3, 360.dp) else maxWidth
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = newPadding,
                modifier = modifier.width(width),
            ) {
                item {
                    WelcomeTitle()
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    AuthForm(
                        apiKey = state.apiKey,
                        submitState = submitState,
                        onApiKeyChanged = apiKeyChanged,
                        onSubmit = onSubmit,
                        onReport = onReport,
                    )
                }
            }
        }
    }
    if (isExpandedWidth) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            list()
        }
    } else {
        list()
    }
}

@Composable
fun WelcomeTitle(
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        AppLogo(size = 100.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(R.string.auth_welcome_message),
            style = typography.displayMedium.copy(fontWeight = FontWeight.Light),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        HyperlinkText(
            stringResource(R.string.comic_vine_auth_prompt),
            items = listOf(
                HyperlinkTextItem(url = stringResource(R.string.comic_vine_api_url))
            ),
            style = typography.bodyLarge,
        )
    }
}

@Composable
fun AuthForm(
    apiKey: String,
    submitState: AuthSubmitState,
    onApiKeyChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        ApiKeyField(
            apiKey = apiKey,
            onApiKeyChanged = onApiKeyChanged,
            isError = submitState is AuthSubmitState.SubmitFailed.EmptyApiKey,
            errorMessage = when (submitState) {
                AuthSubmitState.SubmitFailed.EmptyApiKey -> {
                    stringResource(R.string.comic_vine_api_key_empty_error)
                }

                else -> null
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedSlideContent(
            targetState = submitState,
            alignment = Alignment.Top,
        ) { state ->
            if (state is AuthSubmitState.SubmitFailed) {
                when (state) {
                    is AuthSubmitState.SubmitFailed.SaveError -> {
                        AuthErrorView(
                            error = state.error,
                            onReport = { onReport(it) },
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                    }

                    AuthSubmitState.SubmitFailed.EmptyApiKey -> {}
                }
            }
        }
        SubmitButton(
            state = submitState,
            onSubmit = onSubmit,
        )
    }
}

@Composable
private fun ApiKeyField(
    apiKey: String,
    onApiKeyChanged: (String) -> Unit,
    isError: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = apiKey,
        onValueChange = onApiKeyChanged,
        isError = isError,
        supportingText = errorMessage?.let {
            { Text(errorMessage) }
        },
        placeholder = {
            Text(stringResource(R.string.comic_vine_api_key))
        },
        modifier = modifier,
    )
}

@Composable
private fun SubmitButton(
    state: AuthSubmitState,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onSubmit,
        enabled = state !is AuthSubmitState.Submitted,
        modifier = modifier.fillMaxWidth(0.5f),
    ) {
        when (state) {
            is AuthSubmitState.SubmitInProgress -> {
                CircularProgressIndicator(
                    color = LocalContentColor.current,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
            }

            else -> Text(stringResource(R.string.log_in))
        }
    }
}

@Preview
@Composable
private fun PreviewAuthPage() {
    OpenComicVineTheme {
        Scaffold { contentPadding ->
            Body(
                contentPadding = contentPadding,
                state = AuthState(),
                submitState = AuthSubmitState.Initial,
                isExpandedWidth = false,
                apiKeyChanged = {},
                onSubmit = {},
                onReport = {},
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewAuthPage_Dark() {
    OpenComicVineTheme {
        Scaffold { contentPadding ->
            Body(
                contentPadding = contentPadding,
                state = AuthState(),
                submitState = AuthSubmitState.Initial,
                isExpandedWidth = false,
                apiKeyChanged = {},
                onSubmit = {},
                onReport = {},
            )
        }
    }
}

@Preview("In progress")
@Composable
private fun PreviewAuthForm_InProgress() {
    OpenComicVineTheme {
        AuthForm(
            apiKey = "",
            submitState = AuthSubmitState.SubmitInProgress,
            onApiKeyChanged = {},
            onSubmit = {},
            onReport = {},
        )
    }
}

@Preview("Empty API key error")
@Composable
private fun PreviewAuthForm_EmptyApiKeyError() {
    OpenComicVineTheme {
        AuthForm(
            apiKey = "",
            submitState = AuthSubmitState.SubmitFailed.EmptyApiKey,
            onApiKeyChanged = {},
            onSubmit = {},
            onReport = {},
        )
    }
}

@Preview("Error")
@Composable
private fun PreviewAuthForm_Error() {
    OpenComicVineTheme {
        val snackbarState = remember { SnackbarHostState() }
        CompositionLocalProvider(LocalAppSnackbarState provides snackbarState) {
            AuthForm(
                apiKey = "",
                submitState = AuthSubmitState.SubmitFailed.SaveError(
                    error = ApiKeyRepository.SaveResult.Failed.IO(IOException()),
                ),
                onApiKeyChanged = {},
                onSubmit = {},
                onReport = {},
            )
        }
    }
}

@Preview(
    name = "Expanded width",
    device = "spec:width=1280dp,height=800dp,dpi=480"
)
@Composable
private fun PreviewAuthPage_ExpandedWidth() {
    OpenComicVineTheme {
        Scaffold { contentPadding ->
            Body(
                contentPadding = contentPadding,
                state = AuthState(),
                submitState = AuthSubmitState.Initial,
                isExpandedWidth = true,
                apiKeyChanged = {},
                onSubmit = {},
                onReport = {},
            )
        }
    }
}
