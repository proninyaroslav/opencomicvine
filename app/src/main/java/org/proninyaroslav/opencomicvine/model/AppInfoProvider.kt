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

package org.proninyaroslav.opencomicvine.model

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import org.proninyaroslav.opencomicvine.BuildConfig
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.supportGetPackageInfo
import javax.inject.Inject

interface AppInfoProvider {
    sealed interface State {
        data class Success(
            val appName: String,
            val version: String,
        ) : State

        data class Failed(val exception: Exception) : State
    }

    fun getAppInfo(): State
}

class AppInfoProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppInfoProvider {
    override fun getAppInfo(): AppInfoProvider.State {
        return try {
            context.packageManager.supportGetPackageInfo(context.packageName).run {
                AppInfoProvider.State.Success(
                    appName = context.getString(R.string.app_name),
                    version = versionName ?: BuildConfig.VERSION_NAME,
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
            AppInfoProvider.State.Failed(e)
        }
    }
}
