package org.proninyaroslav.opencomicvine.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTextCardItem(
    text: (@Composable () -> Unit)?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(160.dp)
            .placeholder(
                visible = text == null,
                color = MaterialTheme.colorScheme.surfaceVariant,
                highlight = PlaceholderHighlight.fade()
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 8.dp,
                        top = 16.dp,
                        bottom = 16.dp,
                    ),
            ) {
                ProvideTextStyle(MaterialTheme.typography.titleMedium) {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                    ) {
                        if (text != null) {
                            text()
                        }
                    }
                }
                Icon(
                    painterResource(
                        if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                            R.drawable.ic_chevron_left_24
                        } else {
                            R.drawable.ic_chevron_right_24
                        }
                    ),
                    contentDescription = stringResource(R.string.open),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDetailsTextListItem() {
    OpenComicVineTheme {
        DetailsTextCardItem(
            text = { Text("Item") },
            onClick = {},
            modifier = Modifier.height(160.dp),
        )
    }
}

@Preview(name = "Long text")
@Composable
private fun PreviewDetailsTextListItem_LongText() {
    OpenComicVineTheme {
        DetailsTextCardItem(
            text = { Text("Long Long Long Long Long Long Long Long Long Long Long Long Name") },
            onClick = {},
            modifier = Modifier.height(160.dp),
        )
    }
}

@Preview(name = "Loading")
@Composable
private fun PreviewDetailsTextListItem_Loading() {
    OpenComicVineTheme {
        DetailsTextCardItem(
            text = null,
            onClick = {},
            modifier = Modifier.height(160.dp),
        )
    }
}