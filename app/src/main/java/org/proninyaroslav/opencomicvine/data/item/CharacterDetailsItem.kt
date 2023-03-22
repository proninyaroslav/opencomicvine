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

package org.proninyaroslav.opencomicvine.data.item

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.Flow
import org.proninyaroslav.opencomicvine.data.CharacterDetails
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult

@Immutable
class CharacterDetailsItem(
    val details: CharacterDetails,
    override val isFavorite: Flow<FavoriteFetchResult>,
) : BaseItem {
    override val id: Int
        get() = details.id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterDetailsItem

        if (details != other.details) return false

        return true
    }

    override fun hashCode(): Int = details.hashCode()

    override fun toString(): String =
        "CharacterDetailsItem(details=$details, isFavorite=$isFavorite, id=$id)"
}
