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

package org.proninyaroslav.opencomicvine.ui.components.search_bar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.BackButton
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    placeholder: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    var actionButtonClicked by remember { mutableStateOf(false) }
    val expandedChange: (Boolean) -> Unit = {
        if (actionButtonClicked) {
            actionButtonClicked = false
        } else {
            onActiveChange(it)
        }
    }

    val colors1 = SearchBarDefaults.colors()
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = active,
                onExpandedChange = expandedChange,
                enabled = true,
                placeholder = placeholder,
                leadingIcon = {
                    LeadingButton(
                        active = active,
                        onActiveChange = onActiveChange,
                    )
                },
                trailingIcon = {
                    TrailingButton(
                        query = query,
                        active = active,
                        actions = actions,
                        onQueryChange = onQueryChange,
                    )
                },
                colors = colors1.inputFieldColors,
                interactionSource = null,
            )
        },
        expanded = active,
        onExpandedChange = expandedChange,
        modifier = modifier,
        shape = SearchBarDefaults.inputFieldShape,
        colors = colors1,
        tonalElevation = SearchBarDefaults.TonalElevation,
        shadowElevation = SearchBarDefaults.ShadowElevation,
        windowInsets = SearchBarDefaults.windowInsets,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDockedSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    placeholder: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    var actionButtonClicked by remember { mutableStateOf(false) }
    val expandedChange: (Boolean) -> Unit = {
        if (actionButtonClicked) {
            actionButtonClicked = false
        } else {
            onActiveChange(it)
        }
    }
    val colors1 = SearchBarDefaults.colors()
    DockedSearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = active,
                onExpandedChange = expandedChange,
                enabled = true,
                placeholder = placeholder,
                leadingIcon = {
                    LeadingButton(
                        active = active,
                        onActiveChange = onActiveChange,
                    )
                },
                trailingIcon = {
                    TrailingButton(
                        query = query,
                        active = active,
                        actions = actions,
                        onQueryChange = onQueryChange,
                    )
                },
                colors = colors1.inputFieldColors,
                interactionSource = null,
            )
        },
        expanded = active,
        onExpandedChange = expandedChange,
        modifier = modifier.padding(top = 8.dp),
        shape = SearchBarDefaults.dockedShape,
        colors = colors1,
        tonalElevation = SearchBarDefaults.TonalElevation,
        shadowElevation = SearchBarDefaults.ShadowElevation,
        content = content,
    )
}

@Composable
private fun TrailingButton(
    query: String,
    active: Boolean,
    actions: @Composable RowScope.() -> Unit,
    onQueryChange: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (active && query.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }) {
                Icon(
                    painterResource(R.drawable.ic_clear_24),
                    contentDescription = stringResource(R.string.clear),
                )
            }
        }
        actions()
    }
}

@Composable
private fun LeadingButton(active: Boolean, onActiveChange: (Boolean) -> Unit) {
    if (active) {
        BackButton(onClick = { onActiveChange(false) })
    } else {
        Icon(
            painterResource(R.drawable.ic_search_24),
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun PreviewComicVineSearchBar() {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    OpenComicVineTheme {
        CustomSearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {},
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(stringResource(R.string.search)) },
        ) {}
    }
}

@Preview(name = "Active")
@Composable
private fun PreviewComicVineSearchBar_Active() {
    var query by remember { mutableStateOf("test") }
    var active by remember { mutableStateOf(true) }

    OpenComicVineTheme {
        CustomSearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {},
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(stringResource(R.string.search)) },
        ) {}
    }
}

@Preview
@Composable
private fun PreviewComicVineDockedSearchBar() {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    OpenComicVineTheme {
        CustomDockedSearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {},
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(stringResource(R.string.search)) },
        ) {}
    }
}

@Preview(name = "Active")
@Composable
private fun PreviewComicVineDockedSearchBar_Active() {
    var query by remember { mutableStateOf("test") }
    var active by remember { mutableStateOf(true) }

    OpenComicVineTheme {
        CustomDockedSearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {},
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(stringResource(R.string.search)) },
        ) {}
    }
}
