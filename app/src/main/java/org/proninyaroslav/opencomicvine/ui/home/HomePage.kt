package org.proninyaroslav.opencomicvine.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoriesList
import org.proninyaroslav.opencomicvine.ui.components.error.NetworkNotAvailableSlider
import org.proninyaroslav.opencomicvine.ui.home.category.*
import org.proninyaroslav.opencomicvine.ui.removeBottomPadding
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkConnectionViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkEffect
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkState

sealed interface HomePage {
    object Settings : HomePage
    object RecentCharacters : HomePage
    object RecentIssues : HomePage
    object RecentVolumes : HomePage
    data class Character(val characterId: Int) : HomePage
    data class Issue(val issueId: Int) : HomePage
    data class Volume(val volumeId: Int) : HomePage
    object About : HomePage
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    viewModel: HomeViewModel,
    networkConnection: NetworkConnectionViewModel,
    isExpandedWidth: Boolean,
    onLoadPage: (HomePage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val networkState by networkConnection.state.collectAsStateWithLifecycle()
    val characters = viewModel.miniCharactersList.collectAsLazyPagingItems()
    val issues = viewModel.miniIssuesList.collectAsLazyPagingItems()
    val volumes = viewModel.miniVolumesList.collectAsLazyPagingItems()
    val entities by remember(characters, issues, volumes) {
        derivedStateOf {
            listOf(characters, issues, volumes)
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )
    val showNetworkUnavailable by remember(networkState, entities) {
        derivedStateOf {
            networkState is NetworkState.NoConnection && entities.any { it.itemCount > 0 }
        }
    }

    LaunchedEffect(networkConnection) {
        networkConnection.effect.collect { effect ->
            when (effect) {
                NetworkEffect.Reestablished -> entities.onEach { it.retry() }
            }
        }
    }

    Scaffold(
        topBar = {
            HomeAppBar(
                onLoadPage = onLoadPage,
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        val direction = LocalLayoutDirection.current
        val newContentPadding by remember(contentPadding, isExpandedWidth) {
            derivedStateOf {
                contentPadding.removeBottomPadding(
                    direction = direction,
                    extraHorizontal = if (isExpandedWidth) 16.dp else 0.dp,
                    extraVertical = 16.dp,
                )
            }
        }

//        HomeAppBarMenu(
//            expanded = showMenu,
//            onDismissRequest = { showMenu = false },
//            onLoadPage = onLoadPage,
//        ) {
        CategoriesList(
            isExpandedWidth = isExpandedWidth,
            contentPadding = newContentPadding,
        ) {
            item(
                span = { GridItemSpan(maxLineSpan) },
            ) {
                NetworkNotAvailableSlider(
                    targetState = showNetworkUnavailable,
                    compact = true,
                    modifier = Modifier.padding(
                        if (isExpandedWidth) 0.dp else 16.dp
                    ),
                )
            }

            item {
                IssuesCategory(
                    issues = issues,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(HomePage.RecentIssues) },
                    fullscreen = !isExpandedWidth,
                    onIssueClick = { onLoadPage(HomePage.Issue(it)) },
                    onReport = { viewModel.event(HomeEvent.ErrorReport(it)) },
                )
            }

            item {
                VolumesCategory(
                    volumes = volumes,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(HomePage.RecentVolumes) },
                    fullscreen = !isExpandedWidth,
                    onVolumeClick = { onLoadPage(HomePage.Volume(it)) },
                    onReport = { viewModel.event(HomeEvent.ErrorReport(it)) },
                )
            }

            item {
                CharactersCategory(
                    characters = characters,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(HomePage.RecentCharacters) },
                    fullscreen = !isExpandedWidth,
                    onCharacterClicked = { onLoadPage(HomePage.Character(it)) },
                    onReport = { viewModel.event(HomeEvent.ErrorReport(it)) },
                )
            }
        }
//        }
    }
}