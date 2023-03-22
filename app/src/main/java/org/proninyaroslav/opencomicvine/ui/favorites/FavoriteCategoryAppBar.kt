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

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.ui.components.FilterIconButton
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryAppBar
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteCategoryAppBar(
    title: @Composable () -> Unit,
    onFilterClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CategoryAppBar(
        title = title,
        actions = { FilterIconButton(onClick = onFilterClick) },
        scrollBehavior = scrollBehavior,
        onBackButtonClicked = onBackButtonClicked,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewFavoriteCategoryAppBar() {
    OpenComicVineTheme {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState()
        )
        FavoriteCategoryAppBar(
            title = { Text("Title") },
            onFilterClick = {},
            scrollBehavior = scrollBehavior,
            onBackButtonClicked = {},
        )
    }
}
