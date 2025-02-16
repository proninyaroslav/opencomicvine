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

package org.proninyaroslav.opencomicvine.ui.details.category.issue

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import org.proninyaroslav.opencomicvine.types.item.ConceptItem
import org.proninyaroslav.opencomicvine.types.item.LocationItem
import org.proninyaroslav.opencomicvine.types.item.ObjectItem
import org.proninyaroslav.opencomicvine.types.item.PersonItem
import org.proninyaroslav.opencomicvine.types.item.StoryArcItem
import org.proninyaroslav.opencomicvine.types.item.TeamItem
import org.proninyaroslav.opencomicvine.ui.components.card.CharacterCard
import org.proninyaroslav.opencomicvine.ui.components.card.ConceptCard
import org.proninyaroslav.opencomicvine.ui.components.card.LocationCard
import org.proninyaroslav.opencomicvine.ui.components.card.ObjectCard
import org.proninyaroslav.opencomicvine.ui.components.card.PersonCard
import org.proninyaroslav.opencomicvine.ui.components.card.StoryArcCard
import org.proninyaroslav.opencomicvine.ui.components.card.TeamCard
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.details.DetailsPage
import org.proninyaroslav.opencomicvine.ui.details.DetailsPagerCard
import org.proninyaroslav.opencomicvine.ui.details.DetailsPagerTab
import org.proninyaroslav.opencomicvine.ui.details.DetailsRelatedEntitiesPage
import org.proninyaroslav.opencomicvine.ui.visibilityWrapper

@Immutable
data class IssueOtherInfoState(
    val creators: Flow<PagingData<PersonItem>>,
    val characters: Flow<PagingData<CharacterItem>>,
    val characterDiedIn: Flow<PagingData<CharacterItem>>,
    val teams: Flow<PagingData<TeamItem>>,
    val disbandedTeams: Flow<PagingData<TeamItem>>,
    val locations: Flow<PagingData<LocationItem>>,
    val concepts: Flow<PagingData<ConceptItem>>,
    val objects: Flow<PagingData<ObjectItem>>,
    val storyArcs: Flow<PagingData<StoryArcItem>>,
)

@Composable
fun IssueOtherInfo(
    state: IssueOtherInfoState?,
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

    DetailsPagerCard(
        pagesCount = tabGroup.size,
        pagerState = pagerState,
        tabs = tabGroup.values.toList(),
    ) { page ->
        when (TabGroup.entries[page]) {
            TabGroup.Creators -> CreatorsList(
                creators = pagerState.visibilityWrapper(page) {
                    state?.creators?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.Characters -> CharactersList(
                characters = pagerState.visibilityWrapper(page) {
                    state?.characters?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.DiedCharacters -> CharactersList(
                characters = pagerState.visibilityWrapper(page) {
                    state?.characterDiedIn?.collectAsLazyPagingItems()
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

            TabGroup.DisbandedTeams -> TeamsList(
                teams = pagerState.visibilityWrapper(page) {
                    state?.disbandedTeams?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.Locations -> LocationsList(
                locations = pagerState.visibilityWrapper(page) {
                    state?.locations?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.Concepts -> ConceptsList(
                concepts = pagerState.visibilityWrapper(page) {
                    state?.concepts?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.Objects -> ObjectsList(
                objects = pagerState.visibilityWrapper(page) {
                    state?.objects?.collectAsLazyPagingItems()
                },
                toSourceError = toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = onFavoriteClick,
                onReport = onReport,
            )

            TabGroup.StoryArcs -> StoryArcsList(
                storyArcs = pagerState.visibilityWrapper(page) {
                    state?.storyArcs?.collectAsLazyPagingItems()
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
    Creators,
    Characters,
    DiedCharacters,
    Teams,
    DisbandedTeams,
    Locations,
    Concepts,
    Objects,
    StoryArcs,
}

private val tabGroup = mapOf(
    TabGroup.Creators to DetailsPagerTab(
        text = R.string.issue_creators,
    ),
    TabGroup.Characters to DetailsPagerTab(
        text = R.string.issue_characters,
    ),
    TabGroup.DiedCharacters to DetailsPagerTab(
        text = R.string.issue_died_characters,
    ),
    TabGroup.Teams to DetailsPagerTab(
        text = R.string.issue_teams,
    ),
    TabGroup.DisbandedTeams to DetailsPagerTab(
        text = R.string.issue_disbanded_teams,
    ),
    TabGroup.Locations to DetailsPagerTab(
        text = R.string.issue_locations,
    ),
    TabGroup.Concepts to DetailsPagerTab(
        text = R.string.issue_concepts,
    ),
    TabGroup.Objects to DetailsPagerTab(
        text = R.string.issue_things,
    ),
    TabGroup.StoryArcs to DetailsPagerTab(
        text = R.string.issue_story_arcs,
    ),
)

@Composable
fun CreatorsList(
    creators: LazyPagingItems<PersonItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            PersonCard(
                personInfo = it.info,
                onClick = { onLoadPage(DetailsPage.Person(it.id)) },
            )
        },
        items = creators,
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_person_24,
                label = stringResource(R.string.no_creators),
                compact = true,
            )
        },
        loadingItemCard = {
            PersonCard(
                personInfo = null,
                onClick = {},
            )
        },
        errorMessageTemplates = DetailsRelatedEntitiesPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_creators_list_error_template,
        ),
        onFavoriteClick = { onFavoriteClick(it, FavoriteInfo.EntityType.Person) },
        toSourceError = toSourceError,
        onReport = onReport,
        modifier = modifier,
    )
}

@Composable
fun CharactersList(
    characters: LazyPagingItems<CharacterItem>?,
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
        items = characters,
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_face_24,
                label = stringResource(R.string.no_characters),
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
fun TeamsList(
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

@Composable
fun LocationsList(
    locations: LazyPagingItems<LocationItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            LocationCard(
                locationInfo = it.info,
                onClick = { onLoadPage(DetailsPage.Location(it.id)) },
            )
        },
        items = locations,
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_public_24,
                label = stringResource(R.string.no_locations),
                compact = true,
            )
        },
        loadingItemCard = {
            LocationCard(
                locationInfo = null,
                onClick = {},
            )
        },
        errorMessageTemplates = DetailsRelatedEntitiesPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_locations_list_error_template,
        ),
        onFavoriteClick = { onFavoriteClick(it, FavoriteInfo.EntityType.Location) },
        toSourceError = toSourceError,
        onReport = onReport,
        modifier = modifier,
    )
}

@Composable
fun ConceptsList(
    concepts: LazyPagingItems<ConceptItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            ConceptCard(
                conceptInfo = it.info,
                onClick = { onLoadPage(DetailsPage.Concept(it.id)) },
            )
        },
        items = concepts,
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_menu_book_24,
                label = stringResource(R.string.no_concepts),
                compact = true,
            )
        },
        loadingItemCard = {
            ConceptCard(
                conceptInfo = null,
                onClick = {},
            )
        },
        errorMessageTemplates = DetailsRelatedEntitiesPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_concepts_list_error_template,
        ),
        onFavoriteClick = { onFavoriteClick(it, FavoriteInfo.EntityType.Concept) },
        toSourceError = toSourceError,
        onReport = onReport,
        modifier = modifier,
    )
}

@Composable
fun ObjectsList(
    objects: LazyPagingItems<ObjectItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            ObjectCard(
                objectInfo = it.info,
                onClick = { onLoadPage(DetailsPage.Object(it.id)) },
            )
        },
        items = objects,
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_cube_outline_24,
                label = stringResource(R.string.no_things),
                compact = true,
            )
        },
        loadingItemCard = {
            ObjectCard(
                objectInfo = null,
                onClick = {},
            )
        },
        errorMessageTemplates = DetailsRelatedEntitiesPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_things_list_error_template,
        ),
        onFavoriteClick = { onFavoriteClick(it, FavoriteInfo.EntityType.Object) },
        toSourceError = toSourceError,
        onReport = onReport,
        modifier = modifier,
    )
}

@Composable
fun StoryArcsList(
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
