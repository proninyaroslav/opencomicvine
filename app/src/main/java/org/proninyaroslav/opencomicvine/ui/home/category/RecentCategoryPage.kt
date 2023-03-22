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

package org.proninyaroslav.opencomicvine.ui.home.category

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.item.BaseItem
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.FavoriteButton
import org.proninyaroslav.opencomicvine.ui.components.FavoriteSwipeableBox
import org.proninyaroslav.opencomicvine.ui.components.FilterIconButton
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryAppBar
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterDrawer
import org.proninyaroslav.opencomicvine.ui.components.list.CardCellSize
import org.proninyaroslav.opencomicvine.ui.components.list.PagingVerticalCardGrid
import org.proninyaroslav.opencomicvine.ui.favorites.*
import org.proninyaroslav.opencomicvine.ui.fling
import org.proninyaroslav.opencomicvine.ui.home.RecentErrorView
import org.proninyaroslav.opencomicvine.ui.rememberLazyGridState
import org.proninyaroslav.opencomicvine.ui.removeBottomPadding
import org.proninyaroslav.opencomicvine.ui.viewmodel.*

enum class RecentCategoryPageType {
    Characters,
    Issues,
    Volumes,
}

interface RecentCategoryPage {
    data class ErrorMessageTemplates(
        @StringRes val fetchTemplate: Int,
        @StringRes val saveTemplate: Int,
    )
}

private const val TAG = "RecentCategoryPage"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : BaseItem> RecentCategoryPage(
    modifier: Modifier = Modifier,
    type: RecentCategoryPageType,
    title: @Composable () -> Unit,
    itemCard: @Composable (item: T) -> Unit,
    emptyListPlaceholder: @Composable () -> Unit,
    filterDrawerContent: LazyListScope.() -> Unit,
    items: LazyPagingItems<T>,
    errorMessageTemplates: RecentCategoryPage.ErrorMessageTemplates,
    cellSize: CardCellSize = CardCellSize.Adaptive.Small,
    showApplyButton: Boolean,
    onApplyFilter: () -> Unit,
    viewModel: RecentCategoryPageViewModel,
    networkConnection: NetworkConnectionViewModel,
    favoritesViewModel: FavoritesViewModel,
    onBackButtonClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val snackbarState = LocalAppSnackbarState.current
    val context = LocalContext.current

    LaunchedEffect(networkConnection) {
        networkConnection.effect.collect { effect ->
            when (effect) {
                NetworkEffect.Reestablished -> items.retry()
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RecentCategoryPageEffect.GetFavoriteFailed -> when (val error = effect.error) {
                    is FavoritesRepository.Result.Failed.IO -> {
                        Log.e(TAG, "Unable to get favorites status", error.exception)
                    }
                }
            }
        }
    }

    LaunchedEffect(favoritesViewModel) {
        favoritesViewModel.effect.collect { effect ->
            when (effect) {
                is FavoritesEffect.SwitchFavoriteFailed -> when (val error = effect.error) {
                    is FavoritesRepository.Result.Failed.IO -> coroutineScope.launch {
                        snackbarState.showSnackbar(
                            context.getString(
                                R.string.error_add_delete_from_favorites,
                                error.exception
                            )
                        )
                    }
                }
                is FavoritesEffect.Added -> {}
                is FavoritesEffect.Removed -> {}
            }
        }
    }

    FilterDrawer(
        drawerContent = filterDrawerContent,
        showApplyButton = showApplyButton,
        drawerState = drawerState,
        onClose = { coroutineScope.launch { drawerState.close() } },
        onApply = {
            onApplyFilter()
            coroutineScope.launch { drawerState.close() }
        },
    ) {
        Scaffold(
            topBar = {
                CategoryAppBar(
                    title = title,
                    actions = {
                        FilterIconButton(
                            onClick = { coroutineScope.launch { drawerState.fling() } }
                        )
                    },
                    scrollBehavior = scrollBehavior,
                    onBackButtonClicked = onBackButtonClicked,
                )
            },
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { contentPadding ->
            val direction = LocalLayoutDirection.current
            val newContentPadding by remember(contentPadding) {
                derivedStateOf {
                    contentPadding.removeBottomPadding(
                        direction = direction,
                        extraHorizontal = 16.dp,
                        extraVertical = 16.dp,
                    )
                }
            }
            PagingVerticalCardGrid(
                state = items.rememberLazyGridState(),
                loadState = items.loadState,
                isEmpty = items.itemCount == 0,
                placeholder = emptyListPlaceholder,
                contentPadding = newContentPadding,
                cellSize = cellSize,
                onRefresh = { items.refresh() },
                onError = { state, fullscreen ->
                    RecentErrorView(
                        state = state,
                        toMediatorError = viewModel::toMediatorError,
                        formatFetchErrorMessage = {
                            context.getString(errorMessageTemplates.fetchTemplate, it)
                        },
                        formatSaveErrorMessage = {
                            context.getString(errorMessageTemplates.saveTemplate, it)
                        },
                        onRetry = { items.retry() },
                        onReport = { viewModel.event(RecentCategoryPageEvent.ErrorReport(it)) },
                        compact = !fullscreen,
                    )
                },
                modifier = modifier,
            ) {
                items(
                    count = items.itemCount,
                    key = { index -> items[index]?.id ?: index },
                ) { index ->
                    items[index]?.let {
                        val isFavorite by produceState(initialValue = false, it.isFavorite) {
                            it.isFavorite.collect { res ->
                                value = when (res) {
                                    is FavoriteFetchResult.Success -> res.isFavorite
                                    is FavoriteFetchResult.Failed.IO -> {
                                        Log.e(TAG, "Unable to get favorites status", res.exception)
                                        false
                                    }
                                }
                            }
                        }
                        FavoriteSwipeableBox(
                            isFavorite = isFavorite,
                            icon = {
                                FavoriteButton(
                                    isFavorite = isFavorite,
                                    onClick = {
                                        favoritesViewModel.event(
                                            FavoritesEvent.SwitchFavorite(
                                                entityId = it.id,
                                                entityType = type.toEntityType(),
                                            )
                                        )
                                    },
                                )
                            },
                            actionLabel = stringResource(R.string.add_to_favorite),
                        ) {
                            itemCard(it)
                        }
                    }
                }
            }
        }
    }
}

private fun RecentCategoryPageType.toEntityType(): FavoriteInfo.EntityType = when (this) {
    RecentCategoryPageType.Characters -> FavoriteInfo.EntityType.Character
    RecentCategoryPageType.Issues -> FavoriteInfo.EntityType.Issue
    RecentCategoryPageType.Volumes -> FavoriteInfo.EntityType.Volume
}
