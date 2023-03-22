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

package org.proninyaroslav.opencomicvine.model.paging.wiki

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.IssueInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiIssueItem
import org.proninyaroslav.opencomicvine.data.paging.wiki.WikiIssueItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.preferences.toComicVineFiltersList
import org.proninyaroslav.opencomicvine.data.preferences.toComicVineSort
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.IssuesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingIssueRepository

@AssistedFactory
interface IssuesRemoteMediatorFactory {
    fun create(endOfPaginationOffset: Int? = null): IssuesRemoteMediator
}

class IssuesRemoteMediator @AssistedInject constructor(
    @Assisted private val endOfPaginationOffset: Int?,
    private val issuesRepo: IssuesRepository,
    issuePagingRepo: PagingIssueRepository,
    private val pref: AppPreferences,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : WikiEntityRemoteMediator<PagingWikiIssueItem, IssueInfo, WikiIssueItemRemoteKeys>(
    pagingRepo = issuePagingRepo,
    ioDispatcher = ioDispatcher,
) {
    override suspend fun getEndOfPaginationOffset(): Int? = endOfPaginationOffset

    override suspend fun fetch(offset: Int, limit: Int): FetchResult<IssueInfo> {
        val res = pref.run {
            issuesRepo.getItems(
                offset = offset,
                limit = limit,
                sort = wikiIssuesSort.first().toComicVineSort(),
                filters = wikiIssuesFilters.first().toComicVineFiltersList(),
            )
        }
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> FetchResult.Success(this)
                    else -> FetchResult.Failed(
                        Error.Service(
                            statusCode = statusCode,
                            errorMessage = error,
                        )
                    )
                }
            }
            else -> FetchResult.Failed(
                Error.Fetching(
                    error = res as ComicVineResult.Failed
                )
            )
        }
    }

    override fun buildRemoteKeys(
        values: List<PagingWikiIssueItem>,
        prevOffset: Int?,
        nextOffset: Int?
    ): List<WikiIssueItemRemoteKeys> = values.map { value ->
        WikiIssueItemRemoteKeys(
            id = value.index,
            prevOffset = prevOffset,
            nextOffset = nextOffset,
        )
    }

    override fun buildValues(
        offset: Int,
        fetchList: List<IssueInfo>
    ): List<PagingWikiIssueItem> {
        var index = offset
        return fetchList.map { PagingWikiIssueItem(index = index++, info = it) }.toList()
    }
}
