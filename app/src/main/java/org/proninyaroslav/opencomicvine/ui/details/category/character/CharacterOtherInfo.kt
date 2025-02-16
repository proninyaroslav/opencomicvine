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

package org.proninyaroslav.opencomicvine.ui.details.category.character

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.model.paging.details.DetailsEntitySource
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.types.FavoriteInfo
import org.proninyaroslav.opencomicvine.types.item.CharacterItem
import org.proninyaroslav.opencomicvine.types.item.IssueItem
import org.proninyaroslav.opencomicvine.types.item.MovieItem
import org.proninyaroslav.opencomicvine.types.item.StoryArcItem
import org.proninyaroslav.opencomicvine.types.item.TeamItem
import org.proninyaroslav.opencomicvine.types.item.VolumeItem
import org.proninyaroslav.opencomicvine.ui.components.card.CharacterCard
import org.proninyaroslav.opencomicvine.ui.components.card.IssueCard
import org.proninyaroslav.opencomicvine.ui.components.card.MovieCard
import org.proninyaroslav.opencomicvine.ui.components.card.StoryArcCard
import org.proninyaroslav.opencomicvine.ui.components.card.TeamCard
import org.proninyaroslav.opencomicvine.ui.components.card.VolumeCard
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.details.DetailsPage
import org.proninyaroslav.opencomicvine.ui.details.DetailsPagerCard
import org.proninyaroslav.opencomicvine.ui.details.DetailsPagerTab
import org.proninyaroslav.opencomicvine.ui.details.DetailsRelatedEntitiesPage
import org.proninyaroslav.opencomicvine.ui.visibilityWrapper

@Immutable
data class CharacterOtherInfoState(
    val movies: Flow<PagingData<MovieItem>>,
    val issues: Flow<PagingData<IssueItem>>,
    val volumes: Flow<PagingData<VolumeItem>>,
    val storyArcs: Flow<PagingData<StoryArcItem>>,
    val friends: Flow<PagingData<CharacterItem>>,
    val enemies: Flow<PagingData<CharacterItem>>,
    val teams: Flow<PagingData<TeamItem>>,
    val teamEnemies: Flow<PagingData<TeamItem>>,
    val teamFriends: Flow<PagingData<TeamItem>>,
)

private val IssueCardWidth = 150.dp

@Composable
fun CharacterOtherInfo(
    state: CharacterOtherInfoState?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        tabGroup.size
    }
    val pageHeight by remember(pagerState) {
        derivedStateOf {
            when (TabGroup.entries[pagerState.currentPage]) {
                TabGroup.IssueCredits,
                TabGroup.VolumeCredits -> Modifier.height(IssueCardWidth * 3.3f)

                TabGroup.Movies -> Modifier.height(512.dp)
                else -> Modifier
            }
        }
    }
    DetailsPagerCard(
        pagesCount = tabGroup.size,
        pagerState = pagerState,
        tabs = tabGroup.values.toList(),
        modifier = Modifier.then(pageHeight),
    ) { page ->
        when (TabGroup.entries[page]) {
            TabGroup.Movies -> MoviesList(
                movies = pagerState.visibilityWrapper(page) {
                    state?.movies?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.IssueCredits -> IssuesList(
                issues = pagerState.visibilityWrapper(page) {
                    state?.issues?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.VolumeCredits -> VolumesList(
                volumes = pagerState.visibilityWrapper(page) {
                    state?.volumes?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.StoryArcCredits -> StoryArcsList(
                storyArcs = pagerState.visibilityWrapper(page) {
                    state?.storyArcs?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.Friends -> FriendsList(
                friends = pagerState.visibilityWrapper(page) {
                    state?.friends?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.Enemies -> EnemiesList(
                enemies = pagerState.visibilityWrapper(page) {
                    state?.enemies?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.Teams -> TeamsList(
                teams = pagerState.visibilityWrapper(page) {
                    state?.teams?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.TeamFriends -> TeamsList(
                teams = pagerState.visibilityWrapper(page) {
                    state?.teamFriends?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.TeamEnemies -> TeamsList(
                teams = pagerState.visibilityWrapper(page) {
                    state?.teamEnemies?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )
        }
    }
}

private enum class TabGroup {
    Friends,
    Enemies,
    Teams,
    Movies,
    IssueCredits,
    VolumeCredits,
    StoryArcCredits,
    TeamFriends,
    TeamEnemies,
}

private val tabGroup = mapOf(
    TabGroup.Friends to DetailsPagerTab(
        text = R.string.character_friends,
    ),
    TabGroup.Enemies to DetailsPagerTab(
        text = R.string.character_enemies,
    ),
    TabGroup.Teams to DetailsPagerTab(
        text = R.string.character_teams,
    ),
    TabGroup.Movies to DetailsPagerTab(
        text = R.string.character_movies,
    ),
    TabGroup.IssueCredits to DetailsPagerTab(
        text = R.string.character_issue_credits,
    ),
    TabGroup.VolumeCredits to DetailsPagerTab(
        text = R.string.character_volume_credits,
    ),
    TabGroup.StoryArcCredits to DetailsPagerTab(
        text = R.string.character_story_arc_credits,
    ),
    TabGroup.TeamFriends to DetailsPagerTab(
        text = R.string.character_team_friends,
    ),
    TabGroup.TeamEnemies to DetailsPagerTab(
        text = R.string.character_team_enemies,
    ),
)

@Composable
private fun MoviesList(
    movies: LazyPagingItems<MovieItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            MovieCard(
                movieInfo = it.info,
                onClick = { onLoadPage(DetailsPage.Movie(it.id)) },
            )
        },
        items = movies,
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_movie_24,
                label = stringResource(R.string.no_movies),
                compact = true,
            )
        },
        loadingItemCard = {
            MovieCard(
                movieInfo = null,
                onClick = {},
            )
        },
        errorMessageTemplates = DetailsRelatedEntitiesPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_movies_list_error_template,
        ),
        onFavoriteClick = { onFavoriteClick(it, FavoriteInfo.EntityType.Movie) },
        toSourceError = toSourceError,
        onReport = onReport,
        modifier = modifier,
    )
}

@Composable
private fun IssuesList(
    issues: LazyPagingItems<IssueItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        rowsCount = 1,
        itemCard = {
            IssueCard(
                issueInfo = it.info,
                imageForceStretchHeight = false,
                onClick = { onLoadPage(DetailsPage.Issue(it.id)) },
                modifier = Modifier.width(IssueCardWidth),
            )
        },
        items = issues,
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_menu_book_24,
                label = stringResource(R.string.no_issues),
                compact = true,
            )
        },
        loadingItemCard = {
            IssueCard(
                issueInfo = null,
                imageForceStretchHeight = false,
                onClick = {},
                modifier = Modifier.width(IssueCardWidth),
            )
        },
        errorMessageTemplates = DetailsRelatedEntitiesPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_issues_list_error_template,
        ),
        onFavoriteClick = { onFavoriteClick(it, FavoriteInfo.EntityType.Issue) },
        toSourceError = toSourceError,
        onReport = onReport,
        modifier = modifier,
    )
}

@Composable
private fun VolumesList(
    volumes: LazyPagingItems<VolumeItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        rowsCount = 1,
        itemCard = {
            VolumeCard(
                volumeInfo = it.info,
                imageForceStretchHeight = false,
                onClick = { onLoadPage(DetailsPage.Issue(it.id)) },
                modifier = Modifier.width(IssueCardWidth),
            )
        },
        items = volumes,
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_library_books_24,
                label = stringResource(R.string.no_volumes),
                compact = true,
            )
        },
        loadingItemCard = {
            VolumeCard(
                volumeInfo = null,
                imageForceStretchHeight = false,
                onClick = {},
                modifier = Modifier.width(IssueCardWidth),
            )
        },
        errorMessageTemplates = DetailsRelatedEntitiesPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_volumes_list_error_template,
        ),
        onFavoriteClick = { onFavoriteClick(it, FavoriteInfo.EntityType.Volume) },
        toSourceError = toSourceError,
        onReport = onReport,
        modifier = modifier,
    )
}

@Composable
private fun StoryArcsList(
    storyArcs: LazyPagingItems<StoryArcItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            StoryArcCard(
                storyArcInfo = it.info,
                onClick = { onLoadPage(DetailsPage.StoryArc(it.id)) },
            )
        },
        items = storyArcs,
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_story_arc_24,
                label = stringResource(R.string.no_story_arcs),
                compact = true,
            )
        },
        loadingItemCard = {
            StoryArcCard(
                storyArcInfo = null,
                onClick = {},
            )
        },
        errorMessageTemplates = DetailsRelatedEntitiesPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_story_arcs_list_error_template,
        ),
        onFavoriteClick = { onFavoriteClick(it, FavoriteInfo.EntityType.StoryArc) },
        toSourceError = toSourceError,
        onReport = onReport,
        modifier = modifier,
    )
}

@Composable
private fun FriendsList(
    friends: LazyPagingItems<CharacterItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            CharacterCard(
                characterInfo = it.info,
                onClick = { onLoadPage(DetailsPage.Character(it.id)) },
            )
        },
        items = friends,
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_person_24,
                label = stringResource(R.string.no_friends),
                compact = true,
            )
        },
        loadingItemCard = {
            CharacterCard(
                characterInfo = null,
                onClick = {},
            )
        },
        errorMessageTemplates = DetailsRelatedEntitiesPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_characters_list_error_template,
        ),
        onFavoriteClick = { onFavoriteClick(it, FavoriteInfo.EntityType.Character) },
        toSourceError = toSourceError,
        onReport = onReport,
        modifier = modifier,
    )
}

@Composable
private fun EnemiesList(
    enemies: LazyPagingItems<CharacterItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            CharacterCard(
                characterInfo = it.info,
                onClick = { onLoadPage(DetailsPage.Character(it.id)) },
            )
        },
        items = enemies,
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_person_24,
                label = stringResource(R.string.no_enemies),
                compact = true,
            )
        },
        loadingItemCard = {
            CharacterCard(
                characterInfo = null,
                onClick = {},
            )
        },
        errorMessageTemplates = DetailsRelatedEntitiesPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_characters_list_error_template,
        ),
        onFavoriteClick = { onFavoriteClick(it, FavoriteInfo.EntityType.Character) },
        toSourceError = toSourceError,
        onReport = onReport,
        modifier = modifier,
    )
}

@Composable
private fun TeamsList(
    teams: LazyPagingItems<TeamItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            TeamCard(
                teamInfo = it.info,
                onClick = { onLoadPage(DetailsPage.Team(it.id)) },
            )
        },
        items = teams,
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_groups_24,
                label = stringResource(R.string.no_teams),
                compact = true,
            )
        },
        loadingItemCard = {
            TeamCard(
                teamInfo = null,
                onClick = {},
            )
        },
        errorMessageTemplates = DetailsRelatedEntitiesPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_teams_list_error_template,
        ),
        onFavoriteClick = { onFavoriteClick(it, FavoriteInfo.EntityType.Team) },
        toSourceError = toSourceError,
        onReport = onReport,
        modifier = modifier,
    )
}
