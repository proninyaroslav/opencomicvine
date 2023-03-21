package org.proninyaroslav.opencomicvine.data.item.favorites

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import org.proninyaroslav.opencomicvine.data.TeamInfo
import java.util.*

@Immutable
data class FavoritesTeamItem(
    @Embedded(prefix = "info_")
    val info: TeamInfo,
    override val dateAdded: Date,
) : FavoritesItem {
    override val id: Int
        get() = info.id
}
