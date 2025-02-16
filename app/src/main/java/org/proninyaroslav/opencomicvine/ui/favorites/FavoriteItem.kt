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

package org.proninyaroslav.opencomicvine.ui.favorites

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.components.FavoriteBox
import org.proninyaroslav.opencomicvine.ui.components.FavoriteFilledTonalButton
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun LazyGridItemScope.FavoriteItem(
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BaseFavoriteItem(
        onFavoriteClick = onFavoriteClick,
        modifier = modifier.animateItem(),
        content = content,
    )
}

@Composable
fun LazyItemScope.FavoriteItem(
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BaseFavoriteItem(
        onFavoriteClick = onFavoriteClick,
        modifier = modifier.animateItem(),
        content = content,
    )
}

@Composable
private fun BaseFavoriteItem(
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    FavoriteBox(
        icon = {
            FavoriteFilledTonalButton(
                isFavorite = true,
                onClick = onFavoriteClick,
            )
        },
        modifier = modifier,
        content = content,
    )
}

@Preview
@Composable
private fun PreviewFavoriteItem() {
    OpenComicVineTheme {
        BaseFavoriteItem(
            onFavoriteClick = {},
        ) {
            Card(modifier = Modifier.size(150.dp, 200.dp)) {}
        }
    }
}
