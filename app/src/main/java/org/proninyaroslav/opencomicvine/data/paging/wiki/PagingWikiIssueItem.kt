package org.proninyaroslav.opencomicvine.data.paging.wiki

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.proninyaroslav.opencomicvine.data.IssueInfo
import org.proninyaroslav.opencomicvine.data.paging.ComicVinePagingItem
import org.proninyaroslav.opencomicvine.model.db.converter.DateConverter

@Entity
@TypeConverters(
    DateConverter::class,
)
@Immutable
data class PagingWikiIssueItem(
    @PrimaryKey
    override val index: Int,

    @Embedded(prefix = "info_")
    val info: IssueInfo,
) : ComicVinePagingItem