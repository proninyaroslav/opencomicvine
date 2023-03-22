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

package org.proninyaroslav.opencomicvine.data

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
@Immutable
data class CharacterInfo(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "gender")
    val gender: Gender,

    @Embedded(prefix = "image_")
    @Json(name = "image")
    val image: ImageInfo,

    @Json(name = "date_added")
    val dateAdded: Date,

    @Json(name = "date_last_updated")
    val dateLastUpdated: Date,
)
