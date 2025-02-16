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

package org.proninyaroslav.opencomicvine.ui

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.Gender
import org.proninyaroslav.opencomicvine.types.Origin
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.DateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun TextStyle.calculateTextHeight(maxLines: Int): Dp {
    return with(LocalDensity.current) { lineHeight.toDp() } * maxLines
}

suspend fun DrawerState.fling() = if (isOpen) close() else open()

/*
 * After recreation, LazyPagingItems first return 0 items, then the cached items.
 * This behavior/issue is resetting the LazyGridState scroll position.
 * Below is a workaround. More info: https://issuetracker.google.com/issues/177245496.
 */
@Composable
fun <T : Any> LazyPagingItems<T>.rememberLazyGridState(): LazyGridState {
    return when (itemCount) {
        // Return a different LazyGridState instance.
        0 -> remember(this) { LazyGridState(0, 0) }
        // Return rememberLazyGridState (normal case).
        else -> androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    }
}

/*
 * After recreation, LazyPagingItems first return 0 items, then the cached items.
 * This behavior/issue is resetting the LazyListState scroll position.
 * Below is a workaround. More info: https://issuetracker.google.com/issues/177245496.
 */
@Composable
fun <T : Any> LazyPagingItems<T>.rememberLazyListState(): LazyListState {
    return when (itemCount) {
        // Return a different LazyListState instance.
        0 -> remember(this) { LazyListState(0, 0) }
        // Return rememberLazyState (normal case).
        else -> androidx.compose.foundation.lazy.rememberLazyListState()
    }
}

fun PaddingValues.removeBottomPadding(
    direction: LayoutDirection,
    extraHorizontal: Dp = 0.dp,
    extraVertical: Dp = 0.dp,
): PaddingValues {
    return PaddingValues(
        top = calculateTopPadding() + extraVertical,
        bottom = extraVertical,
        start = calculateStartPadding(direction) + extraHorizontal,
        end = calculateEndPadding(direction) + extraHorizontal,
    )
}

fun PaddingValues.removeBottomPadding(
    direction: LayoutDirection,
    extraPadding: PaddingValues,
): PaddingValues {
    return PaddingValues(
        top = calculateTopPadding() + extraPadding.calculateTopPadding(),
        bottom = extraPadding.calculateBottomPadding(),
        start = calculateStartPadding(direction) + extraPadding.calculateStartPadding(direction),
        end = calculateEndPadding(direction) + extraPadding.calculateEndPadding(direction),
    )
}

fun PaddingValues.addExtraPadding(
    direction: LayoutDirection,
    extraPadding: PaddingValues,
): PaddingValues {
    return PaddingValues(
        top = calculateTopPadding() + extraPadding.calculateTopPadding(),
        bottom = calculateBottomPadding() + extraPadding.calculateBottomPadding(),
        start = calculateStartPadding(direction) + extraPadding.calculateStartPadding(direction),
        end = calculateEndPadding(direction) + extraPadding.calculateEndPadding(direction),
    )
}

fun formatDateRange(start: Date, end: Date): String =
    DateFormat.getDateInstance(DateFormat.SHORT).run {
        "${format(start)} - ${format(end)}"
    }

fun ColorScheme.applyTonalElevation(backgroundColor: Color, elevation: Dp): Color {
    return if (backgroundColor == surface) {
        surfaceColorAtElevation(elevation)
    } else {
        backgroundColor
    }
}

fun LayoutDirection.inverse(): LayoutDirection = when (this) {
    LayoutDirection.Ltr -> LayoutDirection.Rtl
    LayoutDirection.Rtl -> LayoutDirection.Ltr
}

fun Color.toHex(): String {
    val red = this.red * 255
    val green = this.green * 255
    val blue = this.blue * 255
    return String.format("#%02x%02x%02x", red.toInt(), green.toInt(), blue.toInt())
}

fun Bitmap.getBitmapInputStream(
    compressFormat: Bitmap.CompressFormat,
): InputStream {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(compressFormat, 80, byteArrayOutputStream)
    val bitmapData: ByteArray = byteArrayOutputStream.toByteArray()

    return ByteArrayInputStream(bitmapData)
}

fun Uri.getCompressFormatByImageType(): Bitmap.CompressFormat? {
    val url = toString()
    return if (url.lowercase().run { contains(".jpg") || contains(".jpeg") }) {
        Bitmap.CompressFormat.JPEG
    } else if (url.lowercase().run { contains(".png") || contains(".gif") }) {
        Bitmap.CompressFormat.PNG
    } else if (url.lowercase().contains(".webp")) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            Bitmap.CompressFormat.WEBP
        }
    } else {
        null
    }
}

fun Bitmap.CompressFormat.getMimeType(): String = when (this) {
    Bitmap.CompressFormat.JPEG -> "image/jpg"
    Bitmap.CompressFormat.PNG -> "image/png"
    else -> "image/webp"
}

@Composable
fun <T> PagerState.visibilityWrapper(
    page: Int,
    items: @Composable () -> T
): T? =
    if (currentPage == page) {
        items()
    } else {
        null
    }

@Composable
fun Gender.toLocalizedName(): String = when (this) {
    Gender.Other -> stringResource(R.string.gender_other)
    Gender.Male -> stringResource(R.string.gender_male)
    Gender.Female -> stringResource(R.string.gender_female)
}

@Composable
fun Origin.toLocalizedName(): String = when (id) {
    1 -> stringResource(R.string.origin_mutant)
    2 -> stringResource(R.string.origin_cyborg)
    3 -> stringResource(R.string.origin_alien)
    4 -> stringResource(R.string.origin_human)
    5 -> stringResource(R.string.origin_robot)
    6 -> stringResource(R.string.origin_radiation)
    7 -> stringResource(R.string.origin_god_or_eternal)
    8 -> stringResource(R.string.origin_animal)
    9 -> stringResource(R.string.origin_other)
    10 -> stringResource(R.string.origin_infection)
    else -> name
}

/**
 * Settles the app bar by flinging, in case the given velocity is greater than zero, and snapping
 * after the fling settles.
 */
@OptIn(ExperimentalMaterial3Api::class)
suspend fun settleAppBar(
    state: TopAppBarState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?
): Velocity {
    // Check if the app bar is completely collapsed/expanded. If so, no need to settle the app bar,
    // and just return Zero Velocity.
    // Note that we don't check for 0f due to float precision with the collapsedFraction
    // calculation.
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
    }
    // Snap if animation specs were provided.
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 &&
            state.heightOffset > state.heightOffsetLimit
        ) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.heightOffsetLimit
                },
                animationSpec = snapAnimationSpec
            ) { state.heightOffset = value }
        }
    }

    return Velocity(0f, remainingVelocity)
}

@Throws(PackageManager.NameNotFoundException::class)
fun PackageManager.supportGetPackageInfo(packageName: String): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(
            packageName,
            PackageManager.PackageInfoFlags.of(0)
        )
    } else {
        getPackageInfo(packageName, 0)
    }

private val whitespacesRegex = Regex("\\s")

fun String.isMultiWord() = contains(whitespacesRegex)
