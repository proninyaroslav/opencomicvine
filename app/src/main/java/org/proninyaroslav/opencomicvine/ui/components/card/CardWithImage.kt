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

package org.proninyaroslav.opencomicvine.ui.components.card

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.defaultPlaceholder
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun CardWithImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    fallbackImageUrl: String? = null,
    imageDescription: String?,
    @DrawableRes placeholder: Int,
    imageScale: ContentScale = ContentScale.Crop,
    imageForceStretchHeight: Boolean = true,
    imageAspectRatio: Float? = null,
    onClick: () -> Unit,
    onImageWidthChanged: (Dp) -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    var imageWidth by remember { mutableStateOf(120.dp) }

    Card(
        modifier = modifier
            .width(imageWidth)
            .wrapContentHeight(Alignment.Top),
        onClick = onClick,
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            ImageCard(
                imageUrl = imageUrl,
                fallbackImageUrl = fallbackImageUrl,
                imageDescription = imageDescription,
                imageWidth = imageWidth,
                onImageWidthChanged = {
                    imageWidth = it
                    onImageWidthChanged(it)
                },
                placeholder = placeholder,
                imageScale = imageScale,
                imageForceStretchHeight = imageForceStretchHeight,
                imageAspectRatio = imageAspectRatio,
            )
            content()
        }
    }
}

@Preview
@Composable
private fun PreviewCardWithImage() {
    OpenComicVineTheme {
        val titleStyle = MaterialTheme.typography.titleMedium
        LazyColumn {
            item {
                CardWithImage(
                    imageUrl = "https://dummyimage.com/320",
                    imageDescription = "Dummy image",
                    placeholder = R.drawable.placeholder_square,
                    onClick = {},
                ) {
                    Text(
                        "Title",
                        style = titleStyle,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }
}

@Preview(name = "Loading")
@Composable
private fun PreviewCardWithImage_Loading() {
    OpenComicVineTheme {
        val titleStyle = MaterialTheme.typography.titleMedium
        LazyColumn {
            item {
                CardWithImage(
                    imageUrl = null,
                    imageDescription = "Dummy image",
                    placeholder = R.drawable.placeholder_square,
                    onClick = {},
                ) {
                    Text(
                        "",
                        style = titleStyle,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .defaultPlaceholder(visible = true),
                    )
                }
            }
        }
    }
}
