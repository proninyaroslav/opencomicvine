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

package org.proninyaroslav.opencomicvine.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.SearchHistoryInfo
import org.proninyaroslav.opencomicvine.model.repo.SearchHistoryRepository
import org.proninyaroslav.opencomicvine.ui.components.search_bar.CustomDockedSearchBar
import org.proninyaroslav.opencomicvine.ui.components.search_bar.CustomSearchBar
import org.proninyaroslav.opencomicvine.ui.components.search_bar.SearchTopAppBar
import org.proninyaroslav.opencomicvine.ui.components.search_bar.SearchTopAppBarScrollBehavior
import org.proninyaroslav.opencomicvine.ui.search.history.SearchHistory
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.util.*

@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    isSearchSubmitted: Boolean,
    onExpandStateChanged: (Boolean) -> Unit,
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearch: (String) -> Unit,
    actions: @Composable RowScope.() -> Unit,
    history: @Composable () -> Unit,
    isExpandedWidth: Boolean,
    scrollBehavior: SearchTopAppBarScrollBehavior?,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        // Focus on search bar if it's initially expanded
        if (isExpanded) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(isExpanded) {
        if (!isExpanded) {
            focusManager.clearFocus()
        }
    }

    val searchBarModifier = modifier.focusRequester(focusRequester)
    SearchTopAppBar(
        searchBar = {
            if (isExpandedWidth) {
                CustomDockedSearchBar(
                    query = query,
                    onQueryChange = onQueryChanged,
                    onSearch = onSearch,
                    active = isExpanded,
                    onActiveChange = onExpandStateChanged,
                    actions = actions,
                    placeholder = { Placeholder() },
                    modifier = searchBarModifier,
                ) {
                    History(
                        query = query,
                        isSearchSubmitted = isSearchSubmitted,
                        history = history,
                        onSearch = onSearch,
                    )
                }
            } else {
                CustomSearchBar(
                    query = query,
                    onQueryChange = onQueryChanged,
                    onSearch = onSearch,
                    active = isExpanded,
                    onActiveChange = onExpandStateChanged,
                    actions = actions,
                    placeholder = { Placeholder() },
                    modifier = searchBarModifier,
                ) {
                    History(
                        query = query,
                        isSearchSubmitted = isSearchSubmitted,
                        history = history,
                        onSearch = onSearch,
                    )
                }
            }
        },
        isSearchBarExpanded = isExpanded,
        scrollBehavior = if (isExpanded) null else scrollBehavior,
    )
}

@Composable
fun Placeholder(
    modifier: Modifier = Modifier,
) {
    Text(
        stringResource(R.string.search),
        modifier = modifier,
    )
}

@Composable
private fun History(
    query: String,
    isSearchSubmitted: Boolean,
    onSearch: (String) -> Unit,
    history: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(vertical = 8.dp)) {
        if (query.isNotBlank() && !isSearchSubmitted) {
            QuickSearchButton(
                query = query,
                onClick = { onSearch(query) },
            )
        } else {
            history()
        }
    }
}

@Composable
private fun QuickSearchButton(
    query: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = {
            Icon(
                painterResource(R.drawable.ic_search_24),
                contentDescription = null,
            )
        },
        headlineContent = {
            Text(stringResource(R.string.search_for_template, query))
        },
        modifier = modifier.clickable(onClick = onClick),
    )
}

@Preview
@Composable
private fun PreviewSearchView() {
    var query by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    var isSearched by remember { mutableStateOf(false) }

    OpenComicVineTheme {
        SearchView(
            query = query,
            onQueryChanged = {
                isSearched = false
                query = it
            },
            isExpanded = isExpanded,
            isSearchSubmitted = isSearched,
            onExpandStateChanged = { isExpanded = it },
            onSearch = { isSearched = true },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        painterResource(R.drawable.ic_filter_list_24),
                        contentDescription = stringResource(R.string.filter),
                    )
                }
            },
            history = {},
            isExpandedWidth = false,
            scrollBehavior = null,
        )
    }
}

@Preview(name = "Expanded")
@Composable
private fun PreviewSearchView_Expanded() {
    var query by remember { mutableStateOf("test") }
    var isExpanded by remember { mutableStateOf(true) }

    OpenComicVineTheme {
        SearchView(
            query = query,
            onQueryChanged = { query = it },
            isExpanded = isExpanded,
            isSearchSubmitted = false,
            onExpandStateChanged = { isExpanded = it },
            onSearch = {},
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        painterResource(R.drawable.ic_filter_list_24),
                        contentDescription = stringResource(R.string.filter),
                    )
                }
            },
            history = {},
            isExpandedWidth = false,
            scrollBehavior = null,
        )
    }
}

@Preview(name = "Search history")
@Composable
private fun PreviewSearchView_SearchHistory() {
    var query by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(true) }

    OpenComicVineTheme {
        SearchView(
            query = query,
            onQueryChanged = { query = it },
            isExpanded = isExpanded,
            isSearchSubmitted = true,
            onExpandStateChanged = { isExpanded = it },
            onSearch = {},
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        painterResource(R.drawable.ic_filter_list_24),
                        contentDescription = stringResource(R.string.filter),
                    )
                }
            },
            history = {
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
            },
            isExpandedWidth = false,
            scrollBehavior = null,
        )
    }
}

@Preview(
    name = "Expanded width",
    device = "spec:width=1280dp,height=800dp,dpi=480"
)
@Composable
private fun PreviewSearchView_ExpandedWidth() {
    var query by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(true) }

    OpenComicVineTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SearchView(
                query = query,
                isSearchSubmitted = true,
                onQueryChanged = { query = it },
                isExpanded = isExpanded,
                onExpandStateChanged = { isExpanded = it },
                onSearch = {},
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painterResource(R.drawable.ic_filter_list_24),
                            contentDescription = stringResource(R.string.filter),
                        )
                    }
                },
                history = {},
                isExpandedWidth = true,
                scrollBehavior = null,
            )
        }
    }
}
