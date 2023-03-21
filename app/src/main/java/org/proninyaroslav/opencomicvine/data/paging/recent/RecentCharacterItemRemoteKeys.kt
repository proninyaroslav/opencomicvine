package org.proninyaroslav.opencomicvine.data.paging.recent

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.proninyaroslav.opencomicvine.data.paging.ComicVineRemoteKeys

@Entity
data class RecentCharacterItemRemoteKeys(
    @PrimaryKey
    override val id: Int,
    override val prevOffset: Int?,
    override val nextOffset: Int?,
) : ComicVineRemoteKeys
