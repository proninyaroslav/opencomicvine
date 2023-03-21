package org.proninyaroslav.opencomicvine.data

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
@Immutable
data class IssueInfo(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String?,

    @Json(name = "issue_number")
    val issueNumber: String,

    @Embedded(prefix = "volume_")
    @Json(name = "volume")
    val volume: Volume,

    @Embedded(prefix = "image_")
    @Json(name = "image")
    val image: ImageInfo,

    @Json(name = "cover_date")
    val coverDate: Date?,

    @Json(name = "store_date")
    val storeDate: Date?,

    @Json(name = "date_added")
    val dateAdded: Date,

    @Json(name = "date_last_updated")
    val dateLastUpdated: Date,
) {
    @JsonClass(generateAdapter = true)
    data class Volume(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )
}