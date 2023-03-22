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

package org.proninyaroslav.opencomicvine.ui.webview

import androidx.compose.material3.ColorScheme
import org.proninyaroslav.opencomicvine.ui.toHex

class WebViewThemeProvider(private val colorScheme: ColorScheme) {
    fun build(): String =
        """
        body {
            color: ${colorScheme.onSurface.toHex()};
            background-color: ${colorScheme.surface.toHex()};
        }
        a:link {
            color: ${colorScheme.primary.toHex()};
            text-decoration: none;
        }
        a:visited {
            color: ${colorScheme.primary.toHex()};
            text-decoration: none;
        }
    """.trimIndent()
}
