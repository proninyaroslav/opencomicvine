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

import android.annotation.SuppressLint
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.settleAppBar
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    modifier: Modifier = Modifier,
    searchBar: @Composable () -> Unit,
    actions: (@Composable RowScope.() -> Unit)? = null,
    isSearchBarExpanded: Boolean,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: SearchTopAppBarColors = SearchTopAppBarDefaults.searchTopAppBarColors(),
    scrollBehavior: SearchTopAppBarScrollBehavior? = null,
) {
    // Sets the search bar's height offset to collapse the entire bar's height when content is
    // scrolled.
    val heightOffsetLimit = with(LocalDensity.current) { -SearchAppBarContainerHeight.toPx() }
    SideEffect {
        if (scrollBehavior?.state?.heightOffsetLimit != heightOffsetLimit) {
            scrollBehavior?.state?.heightOffsetLimit = heightOffsetLimit
        }
    }

    TopAppBarLayout(
        modifier = modifier
            // clip after padding so we don't show the title over the inset area
            .clipToBounds(),
        scrollBehavior = scrollBehavior,
        actionIconContentColor = colors.actionIconContentColor,
        searchBar = searchBar,
        actions = actions,
        windowInsets = windowInsets,
        isSearchBarExpanded = isSearchBarExpanded,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSearchTopAppBarScrollBehavior(
    state: TopAppBarState = rememberTopAppBarState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay()
): SearchTopAppBarScrollBehavior =
    SearchTopAppBarScrollBehavior(
        state = state,
        snapAnimationSpec = snapAnimationSpec,
        flingAnimationSpec = flingAnimationSpec,
        canScroll = canScroll
    )

/**
 * A [TopAppBarScrollBehavior] that adjusts its properties to affect the colors and height of a top
 * app bar.
 *
 * A top app bar that is set up with this [TopAppBarScrollBehavior] will immediately collapse when
 * the nested content is pulled up, and will immediately appear when the content is pulled down.
 *
 * @param state a [TopAppBarState]
 * @param snapAnimationSpec an optional [AnimationSpec] that defines how the top app bar snaps to
 * either fully collapsed or fully extended state when a fling or a drag scrolled it into an
 * intermediate position
 * @param flingAnimationSpec an optional [DecayAnimationSpec] that defined how to fling the top app
 * bar when the user flings the app bar itself, or the content below it
 * @param canScroll a callback used to determine whether scroll events are to be
 * handled by this [SearchTopAppBarScrollBehavior]
 */
@OptIn(ExperimentalMaterial3Api::class)
class SearchTopAppBarScrollBehavior(
    override val state: TopAppBarState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean = { true }
) : TopAppBarScrollBehavior {
    override val isPinned: Boolean = false
    override var nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (!canScroll()) return Offset.Zero
                val prevHeightOffset = state.heightOffset
                state.heightOffset = state.heightOffset + available.y
                return if (prevHeightOffset != state.heightOffset) {
                    // We're in the middle of top app bar collapse or expand.
                    // Consume only the scroll on the Y axis.
                    available.copy(x = 0f)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (!canScroll()) return Offset.Zero
                state.contentOffset += consumed.y
                if (state.heightOffset == 0f || state.heightOffset == state.heightOffsetLimit) {
                    if (consumed.y == 0f && available.y > 0f) {
                        // Reset the total content offset to zero when scrolling all the way down.
                        // This will eliminate some float precision inaccuracies.
                        state.contentOffset = 0f
                    }
                }
                state.heightOffset = state.heightOffset + consumed.y
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val superConsumed = super.onPostFling(consumed, available)
                return superConsumed + settleAppBar(
                    state,
                    available.y,
                    flingAnimationSpec,
                    snapAnimationSpec
                )
            }
        }
}

@Stable
class SearchTopAppBarColors internal constructor(
    internal val actionIconContentColor: Color,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchTopAppBarColors

        return actionIconContentColor == other.actionIconContentColor
    }

    override fun hashCode(): Int {
        return actionIconContentColor.hashCode()
    }
}

object SearchTopAppBarDefaults {
    @Composable
    fun searchTopAppBarColors(
        actionIconContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ): SearchTopAppBarColors =
        SearchTopAppBarColors(
            actionIconContentColor = actionIconContentColor
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarLayout(
    modifier: Modifier,
    scrollBehavior: SearchTopAppBarScrollBehavior?,
    actionIconContentColor: Color,
    searchBar: @Composable () -> Unit,
    actions: (@Composable RowScope.() -> Unit)?,
    windowInsets: WindowInsets,
    isSearchBarExpanded: Boolean,
) {
    val density = LocalDensity.current
    Layout(
        {
            Box(
                Modifier
                    .layoutId("searchBar")
                    .wrapContentSize()
            ) {
                searchBar()
            }
            if (actions != null && !isSearchBarExpanded) {
                Box(
                    Modifier
                        .layoutId("actionIcons")
                        .padding(end = SearchAppBarHorizontalPadding)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides actionIconContentColor,
                        content = {
                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.windowInsetsPadding(windowInsets),
                                content = actions,
                            )
                        }
                    )
                }
            }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val actionIconsPlaceable =
            measurables.firstOrNull { it.layoutId == "actionIcons" }
                ?.measure(constraints.copy(minWidth = 0))

        val actionIconsPlaceableWidth = actionIconsPlaceable?.width ?: 0
        val maxSearchBarWidth =
            (constraints.maxWidth - actionIconsPlaceableWidth).coerceAtLeast(0)
        val searchBarPlaceable =
            measurables.first { it.layoutId == "searchBar" }
                .measure(constraints.copy(minWidth = 0, maxWidth = maxSearchBarWidth))

        val heightOffsetPx = scrollBehavior?.state?.heightOffset ?: 0f
        val heightOffset = heightOffsetPx.roundToInt()
        val heightPx = density.run {
            searchBarPlaceable.height + heightOffsetPx
        }
        val layoutHeight = heightPx.roundToInt()

        layout(constraints.maxWidth, layoutHeight.coerceAtLeast(0)) {
            // Search bar
            searchBarPlaceable.placeRelative(
                x = if (actionIconsPlaceableWidth > 0) {
                    0
                } else {
                    (constraints.maxWidth - searchBarPlaceable.width) / 2
                },
                y = heightOffset,
            )

            // Action icons
            actionIconsPlaceable?.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = (layoutHeight - actionIconsPlaceable.height) / 2 + heightOffset / 2,
            )
        }
    }
}

private val SearchAppBarHorizontalPadding = 4.dp
private val SearchAppBarContainerHeight = 112.0.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun PreviewSearchTopAppBar() {
    val scrollBehavior = rememberSearchTopAppBarScrollBehavior()

    OpenComicVineTheme {
        Scaffold(
            topBar = {
                SearchTopAppBar(
                    searchBar = {
                        val colors = SearchBarDefaults.colors()
                        SearchBar(
                            inputField = {
                                SearchBarDefaults.InputField(
                                    query = "",
                                    onQueryChange = {},
                                    onSearch = {},
                                    expanded = false,
                                    onExpandedChange = {},
                                    enabled = true,
                                    placeholder = null,
                                    leadingIcon = null,
                                    trailingIcon = null,
                                    colors = colors.inputFieldColors,
                                    interactionSource = null,
                                )
                            },
                            expanded = false,
                            onExpandedChange =  {},
                            modifier = Modifier,
                            shape = SearchBarDefaults.inputFieldShape,
                            colors = colors,
                            tonalElevation = SearchBarDefaults.TonalElevation,
                            shadowElevation = SearchBarDefaults.ShadowElevation,
                            windowInsets = SearchBarDefaults.windowInsets,
                            content =  {},
                        )
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                painterResource(R.drawable.ic_favorite_24),
                                contentDescription = ""
                            )
                        }
                    },
                    isSearchBarExpanded = false,
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {}
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "No actions", showSystemUi = true, showBackground = true)
@Composable
private fun PreviewSearchTopAppBar_NoActions() {
    val scrollBehavior = rememberSearchTopAppBarScrollBehavior()

    OpenComicVineTheme {
        Scaffold(
            topBar = {
                SearchTopAppBar(
                    searchBar = {
                        val colors = SearchBarDefaults.colors()
                        SearchBar(
                            inputField = {
                                SearchBarDefaults.InputField(
                                    query = "",
                                    onQueryChange = {},
                                    onSearch = {},
                                    expanded = false,
                                    onExpandedChange = {},
                                    enabled = true,
                                    placeholder = null,
                                    leadingIcon = null,
                                    trailingIcon = null,
                                    colors = colors.inputFieldColors,
                                    interactionSource = null,
                                )
                            },
                            expanded = false,
                            onExpandedChange =  {},
                            modifier = Modifier,
                            shape = SearchBarDefaults.inputFieldShape,
                            colors = colors,
                            tonalElevation = SearchBarDefaults.TonalElevation,
                            shadowElevation = SearchBarDefaults.ShadowElevation,
                            windowInsets = SearchBarDefaults.windowInsets,
                            content =  {},
                        )
                    },
                    isSearchBarExpanded = false,
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {}
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Expanded search", showSystemUi = true, showBackground = true)
@Composable
private fun PreviewSearchTopAppBar_ExpandedSearch() {
    val scrollBehavior = rememberSearchTopAppBarScrollBehavior()

    OpenComicVineTheme {
        Scaffold(
            topBar = {
                SearchTopAppBar(
                    searchBar = {
                        val colors = SearchBarDefaults.colors()
                        SearchBar(
                            inputField = {
                                SearchBarDefaults.InputField(
                                    query = "",
                                    onQueryChange = {},
                                    onSearch = {},
                                    expanded = false,
                                    onExpandedChange = {},
                                    enabled = true,
                                    placeholder = null,
                                    leadingIcon = null,
                                    trailingIcon = null,
                                    colors = colors.inputFieldColors,
                                    interactionSource = null,
                                )
                            },
                            expanded = false,
                            onExpandedChange =  {},
                            modifier = Modifier,
                            shape = SearchBarDefaults.inputFieldShape,
                            colors = colors,
                            tonalElevation = SearchBarDefaults.TonalElevation,
                            shadowElevation = SearchBarDefaults.ShadowElevation,
                            windowInsets = SearchBarDefaults.windowInsets,
                            content =  {},
                        )
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                painterResource(R.drawable.ic_favorite_24),
                                contentDescription = ""
                            )
                        }
                    },
                    isSearchBarExpanded = true,
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {}
        }
    }
}
