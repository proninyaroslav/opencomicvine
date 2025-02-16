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
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.fade
import io.github.fornewid.placeholder.material3.placeholder
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

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
