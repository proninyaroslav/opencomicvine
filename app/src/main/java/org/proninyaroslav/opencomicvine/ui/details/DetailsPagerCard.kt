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

package org.proninyaroslav.opencomicvine.ui.details

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.CustomTab
import org.proninyaroslav.opencomicvine.ui.components.CustomTabIndicator
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Immutable
data class DetailsPagerTab(
    @StringRes val text: Int,
)

@Composable
fun DetailsPagerCard(
    modifier: Modifier = Modifier,
    pagesCount: Int,
    pagerState: PagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        pagesCount
    },
    tabs: List<DetailsPagerTab>,
    pages: @Composable PagerScope.(page: Int) -> Unit,
) {
    check(pagesCount > 0) { "pagesCount must be greater than 0" }

    val coroutineScope = rememberCoroutineScope()
    val containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    val contentColor = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = modifier.height(464.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
        )
    ) {
        val position = pagerState.currentPage
        ScrollableTabRow(
            selectedTabIndex = position,
            indicator = { tabPositions ->
                CustomTabIndicator(
                    bottomPadding = 0.dp,
                    modifier = Modifier.tabIndicatorOffset(tabPositions[position]),
                )
            },
            divider = {},
            containerColor = containerColor,
            contentColor = contentColor,
        ) {
            tabs.onEachIndexed { index, tab ->
                CustomTab(
                    selected = position == index,
                    text = {
                        Text(
                            stringResource(tab.text),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
        HorizontalDivider()
        HorizontalPager(
            state = pagerState,
            pageContent = pages,
            userScrollEnabled = false,
        )
    }
}

@Preview
@Composable
private fun PreviewDetailsPagerCard() {
    OpenComicVineTheme {
        DetailsPagerCard(
            pagesCount = 3,
            tabs = listOf(
                DetailsPagerTab(
                    text = R.string.character_issue_credits,
                ),
                DetailsPagerTab(
                    text = R.string.character_volume_credits,
                ),
                DetailsPagerTab(
                    text = R.string.character_story_arc_credits,
                )
            ),
        ) { page ->
            when (page) {
                0 -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text("Page 1")
                }

                1 -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text("Page 2")
                }

                2 -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text("Page 3")
                }
            }
        }
    }
}
