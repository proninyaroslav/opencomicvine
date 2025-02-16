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

package org.proninyaroslav.opencomicvine.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

val DefaultCustomBadgeSize = 16.dp

@Composable
fun CustomBadge(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Badge(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = modifier,
        content = content,
    )
}

@Preview
@Composable
private fun PreviewCustomBadge() {
    OpenComicVineTheme {
        CustomBadge {
            Text("100")
        }
    }
}

@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewCustomBadge_Dark() {
    OpenComicVineTheme {
        CustomBadge {
            Text("100")
        }
    }
}
