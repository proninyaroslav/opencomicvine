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

package org.proninyaroslav.opencomicvine.ui.home.category.filter

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentCharactersFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentCharactersFilterBundle
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.state.FilterStateCache
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class CharactersFilterViewModel @Inject constructor(
    private val pref: AppPreferences,
) :
    StoreViewModel<
            CharactersFilterEvent,
            CharactersFilterState,
            CharactersFilterEffect,
            >(CharactersFilterState.Initial) {

    private val stateCache = FilterStateCache<Nothing, PrefRecentCharactersFilterBundle>(
        FilterStateCache.State(filter = CharactersFilterState.Initial.filterBundle)
    )

    init {
        viewModelScope.launch {
            val currentFilters = pref.recentCharactersFilters.first()
            stateCache.save(FilterStateCache.State(filter = currentFilters))
            emitState(
                CharactersFilterState.Loaded(
                    filterBundle = currentFilters,
                )
            )
        }

        on<CharactersFilterEvent.ChangeFilters> { event ->
            emitState(
                CharactersFilterState.FiltersChanged(
                    filterBundle = event.filterBundle,
                    isNeedApply = event.filterBundle != stateCache.current.filter,
                )
            )
        }

        on<CharactersFilterEvent.Apply> {
            val value = state.value
            val newState = CharactersFilterState.Applied(
                filterBundle = value.filterBundle,
            )
            viewModelScope.launch {
                newState.run {
                    pref.setRecentCharactersFilters(filterBundle)
                    stateCache.save(FilterStateCache.State(filter = filterBundle))
                }
                emitEffect(CharactersFilterEffect.Applied)
                emitState(newState)
            }
        }
    }
}

sealed interface CharactersFilterEvent {
    data class ChangeFilters(
        val filterBundle: PrefRecentCharactersFilterBundle,
    ) : CharactersFilterEvent

    object Apply : CharactersFilterEvent
}

sealed interface CharactersFilterState {
    val filterBundle: PrefRecentCharactersFilterBundle

    object Initial : CharactersFilterState {
        override val filterBundle: PrefRecentCharactersFilterBundle =
            PrefRecentCharactersFilterBundle(
                dateAdded = PrefRecentCharactersFilter.DateAdded.Unknown,
            )
    }

    data class Loaded(
        override val filterBundle: PrefRecentCharactersFilterBundle,
    ) : CharactersFilterState

    data class FiltersChanged(
        override val filterBundle: PrefRecentCharactersFilterBundle,
        val isNeedApply: Boolean,
    ) : CharactersFilterState

    data class Applied(
        override val filterBundle: PrefRecentCharactersFilterBundle,
    ) : CharactersFilterState
}

sealed interface CharactersFilterEffect {
    object Applied : CharactersFilterEffect
}
