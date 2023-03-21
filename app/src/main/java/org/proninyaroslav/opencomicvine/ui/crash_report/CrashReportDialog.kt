package org.proninyaroslav.opencomicvine.ui.crash_report

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.error.StackTraceList
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.io.IOException

interface CrashReportDialog {
    @Immutable
    data class Data(
        val comment: String,
    )
}

@Composable
fun CrashReportDialog(
    stackTrace: String,
    onReport: (CrashReportDialog.Data) -> Unit,
    onDismissRequest: () -> Unit,
    isExpandedWidth: Boolean,
    modifier: Modifier = Modifier,
) {
    var comment by rememberSaveable { mutableStateOf("") }

    val containerColor = MaterialTheme.colorScheme.errorContainer
    val contentColor = MaterialTheme.colorScheme.onErrorContainer

    BoxWithConstraints {
        AlertDialog(
            title = { Text(stringResource(R.string.error)) },
            icon = {
                Icon(
                    painterResource(R.drawable.ic_report_24),
                    contentDescription = null,
                )
            },
            text = {
                Body(
                    stackTrace = stackTrace,
                    comment = comment,
                    onCommentChanged = { comment = it },
                )
            },
            confirmButton = {
                CustomTextButton(
                    onClick = { onReport(CrashReportDialog.Data(comment = comment)) },
                ) {
                    Text(stringResource(R.string.report))
                }
            },
            dismissButton = {
                CustomTextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = containerColor,
            iconContentColor = contentColor,
            titleContentColor = contentColor,
            textContentColor = contentColor,
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            ),
            modifier = modifier
                .padding(16.dp)
                .width(if (isExpandedWidth) maxWidth / 2 else maxWidth),
        )
    }
}

@Composable
private fun Body(
    stackTrace: String,
    comment: String,
    onCommentChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var stackTraceExpanded by rememberSaveable { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val snackbarState = LocalAppSnackbarState.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        Text(stringResource(R.string.crash_dialog_summary))
        Text(stringResource(R.string.crash_dialog_extra_info))
        Spacer(Modifier.height(8.dp))
        CustomTextField(
            value = comment,
            onValueChange = onCommentChanged,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(4.dp))
        StackTraceList(
            stackTrace = stackTrace,
            expanded = stackTraceExpanded,
            onHeaderClick = { stackTraceExpanded = !stackTraceExpanded },
            onCopyStackTrace = {
                clipboardManager.setText(AnnotatedString(stackTrace))
                coroutineScope.launch {
                    snackbarState.showSnackbar(
                        context.getString(R.string.copied_to_clipboard),
                    )
                }
            },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
    val contentColor = MaterialTheme.colorScheme.onErrorContainer
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(stringResource(R.string.crash_dialog_comment)) },
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = MaterialTheme.colorScheme.error,
            focusedTextColor = contentColor,
            unfocusedTextColor = contentColor,
            focusedPlaceholderColor = contentColor,
            unfocusedPlaceholderColor = contentColor,
            focusedIndicatorColor = MaterialTheme.colorScheme.error,
            unfocusedIndicatorColor = contentColor,
            containerColor = containerColor,
        ),
        modifier = modifier,
    )
}

@Composable
private fun CustomTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error,
        ),
        onClick = onClick,
        content = content,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PreviewCrashReportDialog() {
    val snackbarState = remember { SnackbarHostState() }
    CompositionLocalProvider(LocalAppSnackbarState provides snackbarState) {
        OpenComicVineTheme {
            CrashReportDialog(
                stackTrace = IOException().stackTraceToString(),
                onReport = {},
                onDismissRequest = {},
                isExpandedWidth = false,
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewCrashReportDialog_Dark() {
    val snackbarState = remember { SnackbarHostState() }
    CompositionLocalProvider(LocalAppSnackbarState provides snackbarState) {
        OpenComicVineTheme {
            CrashReportDialog(
                stackTrace = IOException().stackTraceToString(),
                onReport = {},
                onDismissRequest = {},
                isExpandedWidth = false,
            )
        }
    }
}

@Preview(name = "Expanded", device = Devices.TABLET)
@Composable
private fun PreviewCrashReportDialog_Expanded() {
    val snackbarState = remember { SnackbarHostState() }
    CompositionLocalProvider(LocalAppSnackbarState provides snackbarState) {
        OpenComicVineTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                CrashReportDialog(
                    stackTrace = IOException().stackTraceToString(),
                    onReport = {},
                    onDismissRequest = {},
                    isExpandedWidth = true,
                )
            }
        }
    }
}