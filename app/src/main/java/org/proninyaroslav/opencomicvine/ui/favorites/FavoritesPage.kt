package org.proninyaroslav.opencomicvine.ui.favorites

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoriesList
import org.proninyaroslav.opencomicvine.ui.components.error.NetworkNotAvailableSlider
import org.proninyaroslav.opencomicvine.ui.favorites.category.*
import org.proninyaroslav.opencomicvine.ui.removeBottomPadding
import org.proninyaroslav.opencomicvine.ui.viewmodel.*

sealed interface FavoritesPage {
    object FavoriteCharacters : FavoritesPage
    object FavoriteIssues : FavoritesPage
    object FavoriteVolumes : FavoritesPage
    object FavoriteConcepts : FavoritesPage
    object FavoriteLocations : FavoritesPage
    object FavoriteMovies : FavoritesPage
    object FavoriteObjects : FavoritesPage
    object FavoritePeople : FavoritesPage
    object FavoriteStoryArcs : FavoritesPage
    object FavoriteTeams : FavoritesPage
    data class Character(val characterId: Int) : FavoritesPage
    data class Issue(val issueId: Int) : FavoritesPage
    data class Volume(val volumeId: Int) : FavoritesPage
    data class Concept(val conceptId: Int) : FavoritesPage
    data class Location(val locationId: Int) : FavoritesPage
    data class Movie(val movieId: Int) : FavoritesPage
    data class Object(val objectId: Int) : FavoritesPage
    data class Person(val personId: Int) : FavoritesPage
    data class StoryArc(val storyArcId: Int) : FavoritesPage
    data class Team(val teamId: Int) : FavoritesPage
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesPage(
    viewModel: FavoritesPageViewModel,
    favoritesViewModel: FavoritesViewModel,
    networkConnection: NetworkConnectionViewModel,
    isExpandedWidth: Boolean,
    onLoadPage: (FavoritesPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val networkState by networkConnection.state.collectAsStateWithLifecycle()
    val characters = viewModel.miniCharactersList.collectAsLazyPagingItems()
    val issues = viewModel.miniIssuesList.collectAsLazyPagingItems()
    val volumes = viewModel.miniVolumesList.collectAsLazyPagingItems()
    val concepts = viewModel.miniConceptsList.collectAsLazyPagingItems()
    val locations = viewModel.miniLocationsList.collectAsLazyPagingItems()
    val movies = viewModel.miniMoviesList.collectAsLazyPagingItems()
    val objects = viewModel.miniObjectsList.collectAsLazyPagingItems()
    val people = viewModel.miniPeopleList.collectAsLazyPagingItems()
    val storyArcs = viewModel.miniStoryArcsList.collectAsLazyPagingItems()
    val teams = viewModel.miniTeamsList.collectAsLazyPagingItems()
    val entities by remember(
        characters, issues, volumes, concepts,
        locations, movies, objects, people, storyArcs, teams
    ) {
        derivedStateOf {
            listOf(
                characters,
                issues,
                volumes,
                concepts,
                locations,
                movies,
                objects,
                people,
                storyArcs,
                teams,
            )
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val coroutineScope = rememberCoroutineScope()
    val snackbarState = LocalAppSnackbarState.current
    val context = LocalContext.current

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

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FavoritesPageEffect.Refresh -> coroutineScope.launch {
                    when (effect.entityType) {
                        FavoriteInfo.EntityType.Character -> characters.refresh()
                        FavoriteInfo.EntityType.Issue -> issues.refresh()
                        FavoriteInfo.EntityType.Concept -> concepts.refresh()
                        FavoriteInfo.EntityType.Location -> locations.refresh()
                        FavoriteInfo.EntityType.Movie -> movies.refresh()
                        FavoriteInfo.EntityType.Object -> objects.refresh()
                        FavoriteInfo.EntityType.Person -> people.refresh()
                        FavoriteInfo.EntityType.StoryArc -> storyArcs.refresh()
                        FavoriteInfo.EntityType.Team -> teams.refresh()
                        FavoriteInfo.EntityType.Volume -> volumes.refresh()
                    }
                }
            }
        }
    }

    LaunchedEffect(favoritesViewModel) {
        favoritesViewModel.effect.collect { effect ->
            when (effect) {
                is FavoritesEffect.Added -> {}
                is FavoritesEffect.Removed -> coroutineScope.launch {
                    val res = snackbarState.showSnackbar(
                        context.getString(R.string.removed_from_favorites_message),
                        context.getString(R.string.undo),
                        duration = SnackbarDuration.Short,
                    )
                    if (res == SnackbarResult.ActionPerformed) {
                        favoritesViewModel.event(
                            FavoritesEvent.SwitchFavorite(
                                entityId = effect.entityId,
                                entityType = effect.entityType,
                            )
                        )
                    }
                }
                is FavoritesEffect.SwitchFavoriteFailed -> coroutineScope.launch {
                    snackbarState.showSnackbar(
                        context.getString(
                            R.string.error_add_delete_from_favorites,
                            effect.error,
                        )
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            FavoritesAppBar(scrollBehavior = scrollBehavior)
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
                CharactersCategory(
                    characters = characters,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(FavoritesPage.FavoriteCharacters) },
                    fullscreen = !isExpandedWidth,
                    onCharacterClicked = { onLoadPage(FavoritesPage.Character(it)) },
                    onFavoriteClicked = {
                        favoritesViewModel.event(
                            FavoritesEvent.SwitchFavorite(
                                entityId = it,
                                entityType = FavoriteInfo.EntityType.Character,
                            )
                        )
                    },
                    onReport = { viewModel.event(FavoritesPageEvent.ErrorReport(it)) },
                )
            }

            item {
                IssuesCategory(
                    issues = issues,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(FavoritesPage.FavoriteIssues) },
                    fullscreen = !isExpandedWidth,
                    onIssueClicked = { onLoadPage(FavoritesPage.Issue(it)) },
                    onFavoriteClicked = {
                        favoritesViewModel.event(
                            FavoritesEvent.SwitchFavorite(
                                entityId = it,
                                entityType = FavoriteInfo.EntityType.Issue,
                            )
                        )
                    },
                    onReport = { viewModel.event(FavoritesPageEvent.ErrorReport(it)) },
                )
            }

            item {
                VolumesCategory(
                    volumes = volumes,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(FavoritesPage.FavoriteVolumes) },
                    fullscreen = !isExpandedWidth,
                    onVolumeClicked = { onLoadPage(FavoritesPage.Volume(it)) },
                    onFavoriteClicked = {
                        favoritesViewModel.event(
                            FavoritesEvent.SwitchFavorite(
                                entityId = it,
                                entityType = FavoriteInfo.EntityType.Volume,
                            )
                        )
                    },
                    onReport = { viewModel.event(FavoritesPageEvent.ErrorReport(it)) },
                )
            }

            item {
                ConceptsCategory(
                    concepts = concepts,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(FavoritesPage.FavoriteConcepts) },
                    fullscreen = !isExpandedWidth,
                    onConceptClicked = { onLoadPage(FavoritesPage.Concept(it)) },
                    onFavoriteClicked = {
                        favoritesViewModel.event(
                            FavoritesEvent.SwitchFavorite(
                                entityId = it,
                                entityType = FavoriteInfo.EntityType.Concept,
                            )
                        )
                    },
                    onReport = { viewModel.event(FavoritesPageEvent.ErrorReport(it)) },
                )
            }

            item {
                LocationsCategory(
                    locations = locations,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(FavoritesPage.FavoriteLocations) },
                    fullscreen = !isExpandedWidth,
                    onLocationClicked = { onLoadPage(FavoritesPage.Location(it)) },
                    onFavoriteClicked = {
                        favoritesViewModel.event(
                            FavoritesEvent.SwitchFavorite(
                                entityId = it,
                                entityType = FavoriteInfo.EntityType.Location,
                            )
                        )
                    },
                    onReport = { viewModel.event(FavoritesPageEvent.ErrorReport(it)) },
                )
            }

            item {
                MoviesCategory(
                    movies = movies,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(FavoritesPage.FavoriteMovies) },
                    fullscreen = !isExpandedWidth,
                    onMovieClicked = { onLoadPage(FavoritesPage.Movie(it)) },
                    onFavoriteClicked = {
                        favoritesViewModel.event(
                            FavoritesEvent.SwitchFavorite(
                                entityId = it,
                                entityType = FavoriteInfo.EntityType.Movie,
                            )
                        )
                    },
                    onReport = { viewModel.event(FavoritesPageEvent.ErrorReport(it)) },
                )
            }

            item {
                ObjectsCategory(
                    objects = objects,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(FavoritesPage.FavoriteObjects) },
                    fullscreen = !isExpandedWidth,
                    onObjectClicked = { onLoadPage(FavoritesPage.Object(it)) },
                    onFavoriteClicked = {
                        favoritesViewModel.event(
                            FavoritesEvent.SwitchFavorite(
                                entityId = it,
                                entityType = FavoriteInfo.EntityType.Object,
                            )
                        )
                    },
                    onReport = { viewModel.event(FavoritesPageEvent.ErrorReport(it)) },
                )
            }

            item {
                PeopleCategory(
                    people = people,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(FavoritesPage.FavoritePeople) },
                    fullscreen = !isExpandedWidth,
                    onPersonClicked = { onLoadPage(FavoritesPage.Person(it)) },
                    onFavoriteClicked = {
                        favoritesViewModel.event(
                            FavoritesEvent.SwitchFavorite(
                                entityId = it,
                                entityType = FavoriteInfo.EntityType.Person,
                            )
                        )
                    },
                    onReport = { viewModel.event(FavoritesPageEvent.ErrorReport(it)) },
                )
            }

            item {
                StoryArcsCategory(
                    storyArcs = storyArcs,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(FavoritesPage.FavoriteStoryArcs) },
                    fullscreen = !isExpandedWidth,
                    onStoryArcClicked = { onLoadPage(FavoritesPage.StoryArc(it)) },
                    onFavoriteClicked = {
                        favoritesViewModel.event(
                            FavoritesEvent.SwitchFavorite(
                                entityId = it,
                                entityType = FavoriteInfo.EntityType.StoryArc,
                            )
                        )
                    },
                    onReport = { viewModel.event(FavoritesPageEvent.ErrorReport(it)) },
                )
            }

            item {
                TeamsCategory(
                    teams = teams,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(FavoritesPage.FavoriteTeams) },
                    fullscreen = !isExpandedWidth,
                    onTeamClicked = { onLoadPage(FavoritesPage.Team(it)) },
                    onFavoriteClicked = {
                        favoritesViewModel.event(
                            FavoritesEvent.SwitchFavorite(
                                entityId = it,
                                entityType = FavoriteInfo.EntityType.Team,
                            )
                        )
                    },
                    onReport = { viewModel.event(FavoritesPageEvent.ErrorReport(it)) },
                )
            }
        }
    }
}