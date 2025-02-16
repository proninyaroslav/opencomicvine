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

package org.proninyaroslav.opencomicvine.ui.search.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.SearchHistoryInfo
import org.proninyaroslav.opencomicvine.model.repo.SearchHistoryRepository
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.io.IOException
import java.util.*

@Composable
fun SearchHistory(
    historyList: SearchHistoryRepository.Result<List<SearchHistoryInfo>>,
    onClick: (SearchHistoryInfo) -> Unit,
    onRemove: (SearchHistoryInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (historyList) {
        is SearchHistoryRepository.Result.Success -> historyList.data.let {
            if (it.isEmpty()) {
                Placeholder()
            } else {
                HistoryList(
                    list = it,
                    onClick = onClick,
                    onRemove = onRemove,
                    modifier = modifier,
                )
            }
        }
        is SearchHistoryRepository.Result.Failed.IO -> {
            SearchHistoryErrorView(
                error = historyList,
            )
        }
    }
}

@Composable
private fun Placeholder(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        EmptyListPlaceholder(
            icon = R.drawable.ic_history_24,
            label = stringResource(R.string.empty_search_history),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
fun HistoryList(
    list: List<SearchHistoryInfo>,
    onClick: (SearchHistoryInfo) -> Unit,
    onRemove: (SearchHistoryInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(list, key = { it.query }) {
            HistoryItem(
                info = it,
                onClick = { onClick(it) },
                onRemove = { onRemove(it) },
            )
        }
    }
}

@Composable
private fun LazyItemScope.HistoryItem(
    info: SearchHistoryInfo,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = {
            Icon(
                painterResource(R.drawable.ic_history_24),
                contentDescription = null,
            )
        },
        headlineContent = { Text(info.query) },
        trailingContent = {
            IconButton(onClick = onRemove) {
                Icon(
                    painterResource(R.drawable.ic_clear_24),
                    contentDescription = null,
                )
            }
        },
        modifier = modifier
            .clickable(onClick = onClick)
            .animateItem(),
    )
}

@Preview
@Composable
fun PreviewSearchHistory() {
    OpenComicVineTheme {
        SearchHistory(
            historyList = SearchHistoryRepository.Result.Success(
                listOf(
                    SearchHistoryInfo(
                        query = "Batman",
                        date = Date(
                            GregorianCalendar(2022, 0, 3).timeInMillis
                        ),
                    ),
                    SearchHistoryInfo(
                        query = "Spider Man",
                        date = Date(
                            GregorianCalendar(2022, 0, 2).timeInMillis
                        ),
                    ),
                    SearchHistoryInfo(
                        query = "Superman",
                        date = Date(
                            GregorianCalendar(2022, 0, 1).timeInMillis
                        ),
                    ),
                )
            ),
            onClick = {},
            onRemove = {},
        )
    }
}

@Preview(name = "Fetch error")
@Composable
fun PreviewSearchHistory_FetchError() {
    val snackbarState = remember { SnackbarHostState() }
    CompositionLocalProvider(LocalAppSnackbarState provides snackbarState) {
        OpenComicVineTheme {
            SearchHistory(
                historyList = SearchHistoryRepository.Result.Failed.IO(IOException()),
                onClick = {},
                onRemove = {},
            )
        }
    }
}
