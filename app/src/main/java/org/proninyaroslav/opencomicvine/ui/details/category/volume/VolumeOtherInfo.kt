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

package org.proninyaroslav.opencomicvine.ui.details.category.volume

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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
import org.proninyaroslav.opencomicvine.types.item.volume.VolumeCharacterItem
import org.proninyaroslav.opencomicvine.types.item.volume.VolumeConceptItem
import org.proninyaroslav.opencomicvine.types.item.volume.VolumeLocationItem
import org.proninyaroslav.opencomicvine.types.item.volume.VolumeObjectItem
import org.proninyaroslav.opencomicvine.types.item.volume.VolumePersonItem
import org.proninyaroslav.opencomicvine.ui.calculateTextHeight
import org.proninyaroslav.opencomicvine.ui.components.card.CharacterCard
import org.proninyaroslav.opencomicvine.ui.components.card.ConceptCard
import org.proninyaroslav.opencomicvine.ui.components.card.LocationCard
import org.proninyaroslav.opencomicvine.ui.components.card.ObjectCard
import org.proninyaroslav.opencomicvine.ui.components.card.PersonCard
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.details.DetailsPage
import org.proninyaroslav.opencomicvine.ui.details.DetailsPagerCard
import org.proninyaroslav.opencomicvine.ui.details.DetailsPagerTab
import org.proninyaroslav.opencomicvine.ui.details.DetailsRelatedEntitiesPage
import org.proninyaroslav.opencomicvine.ui.visibilityWrapper

@Immutable
data class VolumeOtherInfoState(
    val characters: Flow<PagingData<VolumeCharacterItem>>,
    val creators: Flow<PagingData<VolumePersonItem>>,
    val locations: Flow<PagingData<VolumeLocationItem>>,
    val concepts: Flow<PagingData<VolumeConceptItem>>,
    val objects: Flow<PagingData<VolumeObjectItem>>,
)

@Composable
fun VolumeOtherInfo(
    state: VolumeOtherInfoState?,
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
        modifier = Modifier.height(516.dp),
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
        }
    }
}

private enum class TabGroup {
    Creators,
    Characters,
    Locations,
    Concepts,
    Objects,
}

private val tabGroup = mapOf(
    TabGroup.Creators to DetailsPagerTab(
        text = R.string.volume_creators,
    ),
    TabGroup.Characters to DetailsPagerTab(
        text = R.string.volume_characters,
    ),
    TabGroup.Locations to DetailsPagerTab(
        text = R.string.volume_locations,
    ),
    TabGroup.Concepts to DetailsPagerTab(
        text = R.string.volume_concepts,
    ),
    TabGroup.Objects to DetailsPagerTab(
        text = R.string.volume_things,
    ),
)

@Composable
private fun CharactersList(
    characters: LazyPagingItems<VolumeCharacterItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            CharacterCard(
                item = it,
                onLoadPage = onLoadPage,
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
private fun CharacterCard(
    item: VolumeCharacterItem,
    onLoadPage: (DetailsPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    item.run {
        CharacterCard(
            characterInfo = info,
            additionalInfo = { CountOfAppearances(count = countOfAppearances) },
            onClick = { onLoadPage(DetailsPage.Character(id)) },
            modifier = modifier,
        )
    }
}

@Composable
private fun CreatorsList(
    creators: LazyPagingItems<VolumePersonItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            PersonCard(
                item = it,
                onLoadPage = onLoadPage,
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
private fun PersonCard(
    item: VolumePersonItem,
    onLoadPage: (DetailsPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    item.run {
        PersonCard(
            personInfo = info,
            additionalInfo = { CountOfAppearances(count = countOfAppearances) },
            onClick = { onLoadPage(DetailsPage.Person(id)) },
            modifier = modifier,
        )
    }
}

@Composable
private fun LocationsList(
    locations: LazyPagingItems<VolumeLocationItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            LocationCard(
                item = it,
                onLoadPage = onLoadPage,
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
private fun LocationCard(
    item: VolumeLocationItem,
    onLoadPage: (DetailsPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    item.run {
        LocationCard(
            locationInfo = info,
            additionalInfo = { CountOfAppearances(count = countOfAppearances) },
            onClick = { onLoadPage(DetailsPage.Location(id)) },
            modifier = modifier,
        )
    }
}

@Composable
private fun ConceptsList(
    concepts: LazyPagingItems<VolumeConceptItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            ConceptCard(
                item = it,
                onLoadPage = onLoadPage,
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
private fun ConceptCard(
    item: VolumeConceptItem,
    onLoadPage: (DetailsPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    item.run {
        ConceptCard(
            conceptInfo = info,
            additionalInfo = { CountOfAppearances(count = countOfAppearances) },
            onClick = { onLoadPage(DetailsPage.Concept(id)) },
            modifier = modifier,
        )
    }
}

@Composable
private fun ObjectsList(
    objects: LazyPagingItems<VolumeObjectItem>?,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int, FavoriteInfo.EntityType) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsRelatedEntitiesPage(
        itemCard = {
            ObjectCard(
                item = it,
                onLoadPage = onLoadPage,
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
private fun ObjectCard(
    item: VolumeObjectItem,
    onLoadPage: (DetailsPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    item.run {
        ObjectCard(
            objectInfo = info,
            additionalInfo = { CountOfAppearances(count = countOfAppearances) },
            onClick = { onLoadPage(DetailsPage.Object(id)) },
            modifier = modifier,
        )
    }
}

@Composable
fun CountOfAppearances(
    count: Int,
    modifier: Modifier = Modifier,
) {
    val labelStyle = MaterialTheme.typography.bodyMedium
    val labelMaxLines = 1

    Spacer(modifier = Modifier.height(4.dp))
    Text(
        pluralStringResource(
            R.plurals.details_volume_count_of_appearances_template,
            count,
            count,
        ),
        style = labelStyle,
        maxLines = labelMaxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .height(
                labelStyle.calculateTextHeight(maxLines = labelMaxLines)
            ),
    )
}
