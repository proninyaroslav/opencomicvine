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

import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.fade
import io.github.fornewid.placeholder.material3.placeholder
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.ImageInfo
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

private const val TAG = "DetailsImage"

@Composable
fun DetailsImage(
    image: ImageInfo?,
    imageDescription: String?,
    isExpandedWidth: Boolean,
    onClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsImage(
        imageUrl = if (isExpandedWidth) image?.mediumUrl else image?.smallUrl,
        fallbackImageUrl = image?.originalUrl,
        imageDescription = imageDescription,
        isExpandedWidth = isExpandedWidth,
        onClick = { image?.originalUrl?.let { onClick(it) } },
        modifier = modifier,
    )
}

@Composable
fun DetailsImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    fallbackImageUrl: String? = null,
    imageDescription: String?,
    isExpandedWidth: Boolean,
    onClick: (url: String) -> Unit,
) {
    val placeholder = if (isExpandedWidth) {
        R.drawable.placeholder_small
    } else {
        R.drawable.placeholder_medium
    }
    var imageWidth by remember {
        mutableStateOf(
            if (isExpandedWidth) DefaultImageWidthExpanded else DefaultImageWidth
        )
    }
    var imageHeight by remember { mutableStateOf(imageWidth * 1.6f) }
    var showImageBorder by remember { mutableStateOf(false) }

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        border = if (showImageBorder) {
            CardDefaults.outlinedCardBorder()
        } else {
            BorderStroke(0.dp, Color.Transparent)
        },
        onClick = { imageUrl?.let { onClick(imageUrl) } },
        modifier = modifier.size(width = imageWidth, height = imageHeight),
    ) {
        if (imageUrl == null) {
            Placeholder(
                image = placeholder,
                contentDescription = imageDescription,
                isHighlightVisible = true,
            )
        } else {
            val density = LocalDensity.current
            var url by remember { mutableStateOf(imageUrl) }

            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                loading = {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Success) {
                        SubcomposeAsyncImageContent()
                    } else {
                        Placeholder(
                            image = placeholder,
                            contentDescription = imageDescription,
                            isHighlightVisible = state !is AsyncImagePainter.State.Error,
                        )
                    }
                },
                error = {
                    Placeholder(
                        image = placeholder,
                        contentDescription = imageDescription,
                        isHighlightVisible = false,
                    )
                },
                onSuccess = { showImageBorder = true },
                onError = {
                    Log.d(TAG, "Unable to load image $imageUrl", it.result.throwable)
                    if (url != fallbackImageUrl && fallbackImageUrl != null) {
                        url = fallbackImageUrl
                    }
                },
                contentDescription = imageDescription,
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                modifier = Modifier
                    .onGloballyPositioned {
                        val (newWidth, newHeight) = with(density) {
                            it.size.run { width.toDp() to height.toDp() }
                        }
                        if (newWidth != imageWidth) {
                            imageWidth = newWidth
                        }
                        if (newHeight != imageHeight) {
                            imageHeight = newHeight
                        }
                    }
            )
        }
    }
}

@Composable
private fun Placeholder(
    @DrawableRes image: Int,
    contentDescription: String?,
    isHighlightVisible: Boolean,
) {
    Image(
        painterResource(image),
        contentDescription = contentDescription,
        modifier = Modifier
            .placeholder(
                visible = isHighlightVisible,
                color = Color.Transparent,
                highlight = PlaceholderHighlight.fade()
            )
            .fillMaxSize()
    )
}

private val DefaultImageWidth = 120.dp
private val DefaultImageWidthExpanded = 192.dp

@Preview
@Composable
private fun PreviewDetailsMainImage() {
    OpenComicVineTheme {
        DetailsImage(
            image = ImageInfo(
                iconUrl = "",
                mediumUrl = "https://comicvine.gamespot.com/a/uploads/scale_medium/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                screenUrl = "",
                screenLargeUrl = "",
                smallUrl = "https://comicvine.gamespot.com/a/uploads/scale_small/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                superUrl = "",
                thumbUrl = "",
                tinyUrl = "",
                originalUrl = "https://comicvine.gamespot.com/a/uploads/original/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                imageTags = null,
            ),
            imageDescription = null,
            isExpandedWidth = false,
            onClick = {},
        )
    }
}

@Preview(name = "Loading")
@Composable
private fun PreviewDetailsMainImage_Loading() {
    OpenComicVineTheme {
        DetailsImage(
            image = null,
            imageDescription = null,
            isExpandedWidth = false,
            onClick = {},
        )
    }
}

@Preview(
    name = "Expanded width",
    uiMode = UI_MODE_TYPE_NORMAL,
    device = "spec:width=1280dp,height=800dp,dpi=480"
)
@Composable
private fun PreviewDetailsMainImage_ExpandedWidth() {
    OpenComicVineTheme {
        DetailsImage(
            image = ImageInfo(
                iconUrl = "",
                mediumUrl = "https://comicvine.gamespot.com/a/uploads/scale_medium/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                screenUrl = "",
                screenLargeUrl = "",
                smallUrl = "https://comicvine.gamespot.com/a/uploads/scale_small/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                superUrl = "",
                thumbUrl = "",
                tinyUrl = "",
                originalUrl = "https://comicvine.gamespot.com/a/uploads/original/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                imageTags = null,
            ),
            imageDescription = null,
            isExpandedWidth = true,
            onClick = {},
        )
    }
}
