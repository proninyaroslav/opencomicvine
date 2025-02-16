/*
 * Copyright 2021 The Android Open Source Project
 * Copyright 2022 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.proninyaroslav.opencomicvine.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.fade
import io.github.fornewid.placeholder.material3.placeholder
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.applyTonalElevation
import org.proninyaroslav.opencomicvine.ui.calculateTextHeight
import org.proninyaroslav.opencomicvine.ui.settleAppBar
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import kotlin.math.max
import kotlin.math.roundToInt

private const val TAG = "TopAppBarWithImage"

val TopAppBarWithImageTitleStyle
    @Composable
    get() = MaterialTheme.typography.headlineMedium
val TopAppBarWithImageSmallTitleStyle
    @Composable
    get() = MaterialTheme.typography.titleLarge

/**
 * Top app bars display information, actions and image at the top of a screen.
 *
 * A large top app bar that uses a [scrollBehavior] to customize its nested scrolling behavior when
 * working in conjunction with scrolling content.
 *
 * @param title the title to be displayed in the top app bar. This title will be used in the app
 * bar's expanded states.
 * @param smallTitle the title to be displayed in the top app bar. This title will be used in the app
 * bar's collapsed states, in its collapsed state it will be composed with a
 * smaller sized [TextStyle]
 * @param imageUrl the URL of image, that will be displayed in the app bar's expanded states.
 * @param modifier the [Modifier] to be applied to this top app bar
 * @param navigationIcon the navigation icon displayed at the start of the top app bar. This should
 * typically be an [IconButton] or [IconToggleButton].
 * @param actions the actions displayed at the end of the top app bar. This should typically be
 * [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param windowInsets a window insets that app bar will respect.
 * @param maxBottomPinnedHeight the maximum size of the lower part of the top bar
 * in the expanded state. Can be used if more space is needed for the [title].
 * @param colors [TopAppBarWithImageColors] that will be used to resolve the colors used for this top app
 * bar in different states. See [TopAppBarWithImageDefaults.topAppBarColors].
 * @param scrollBehavior a [TopAppBarScrollBehavior] which holds various offset values that will be
 * applied by this top app bar to set up its height and colors. A scroll behavior is designed to
 * work in conjunction with a scrolled content to change the top app bar appearance as the content
 * scrolls. See [TopAppBarScrollBehavior.nestedScrollConnection].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithImage(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    smallTitle: @Composable () -> Unit = title,
    imageUrl: String?,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    maxBottomPinnedHeight: Dp = TopAppBarPinnedHeight,
    colors: TopAppBarWithImageColors = TopAppBarWithImageDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarWithImageScrollBehavior? = null
) {
    TwoRowsTopAppBar(
        title = title,
        titleTextStyle = TopAppBarWithImageTitleStyle,
        smallTitleTextStyle = TopAppBarWithImageSmallTitleStyle,
        titleBottomPadding = TopAppBarBottomPadding,
        smallTitle = smallTitle,
        imageUrl = imageUrl,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = colors,
        windowInsets = windowInsets,
        maxHeight = if (maxBottomPinnedHeight > TopAppBarPinnedHeight) {
            TopAppBarMaxHeight + (maxBottomPinnedHeight - TopAppBarPinnedHeight)
        } else {
            TopAppBarMaxHeight
        },
        topPinnedHeight = TopAppBarPinnedHeight,
        bottomPinnedHeight = if (maxBottomPinnedHeight < TopAppBarPinnedHeight) {
            TopAppBarPinnedHeight
        } else {
            maxBottomPinnedHeight
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberTopAppBarWithImageScrollBehavior(
    state: TopAppBarState = rememberTopAppBarState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay()
): TopAppBarWithImageScrollBehavior =
    TopAppBarWithImageScrollBehavior(
        state = state,
        snapAnimationSpec = snapAnimationSpec,
        flingAnimationSpec = flingAnimationSpec,
        canScroll = canScroll
    )

/**
 * A [TopAppBarScrollBehavior] that adjusts its properties to affect the colors and height of a top
 * app bar.
 *
 * A top app bar that is set up with this [TopAppBarScrollBehavior] will immediately collapse when
 * the nested content is pulled up, and will expand back the collapsed area when the content is
 * pulled all the way down.
 *
 * @param state a [TopAppBarState]
 * @param snapAnimationSpec an optional [AnimationSpec] that defines how the top app bar snaps to
 * either fully collapsed or fully extended state when a fling or a drag scrolled it into an
 * intermediate position
 * @param flingAnimationSpec an optional [DecayAnimationSpec] that defined how to fling the top app
 * bar when the user flings the app bar itself, or the content below it
 * @param canScroll a callback used to determine whether scroll events are to be
 * handled by this [TopAppBarWithImageScrollBehavior]
 */
@OptIn(ExperimentalMaterial3Api::class)
class TopAppBarWithImageScrollBehavior constructor(
    override val state: TopAppBarState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean = { true }
) : TopAppBarScrollBehavior {
    override val isPinned: Boolean = false
    override var nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Don't intercept if scrolling down.
                if (!canScroll() || available.y > 0f) return Offset.Zero

                val prevHeightOffset = state.heightOffset
                state.heightOffset = state.heightOffset + available.y
                return if (prevHeightOffset != state.heightOffset) {
                    // We're in the middle of top app bar collapse or expand.
                    // Consume only the scroll on the Y axis.
                    available.copy(x = 0f)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (!canScroll()) return Offset.Zero
                state.contentOffset += consumed.y

                if (available.y < 0f || consumed.y < 0f) {
                    // When scrolling up, just update the state's height offset.
                    val oldHeightOffset = state.heightOffset
                    state.heightOffset = state.heightOffset + consumed.y
                    return Offset(0f, state.heightOffset - oldHeightOffset)
                }

                if (consumed.y == 0f && available.y > 0) {
                    // Reset the total content offset to zero when scrolling all the way down. This
                    // will eliminate some float precision inaccuracies.
                    state.contentOffset = 0f
                }

                if (available.y > 0f) {
                    // Adjust the height offset in case the consumed delta Y is less than what was
                    // recorded as available delta Y in the pre-scroll.
                    val oldHeightOffset = state.heightOffset
                    state.heightOffset = state.heightOffset + available.y
                    return Offset(0f, state.heightOffset - oldHeightOffset)
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val superConsumed = super.onPostFling(consumed, available)
                return superConsumed + settleAppBar(
                    state,
                    available.y,
                    flingAnimationSpec,
                    snapAnimationSpec
                )
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TwoRowsTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    titleBottomPadding: Dp,
    smallTitle: @Composable () -> Unit,
    smallTitleTextStyle: TextStyle,
    imageUrl: String?,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    windowInsets: WindowInsets,
    colors: TopAppBarWithImageColors,
    maxHeight: Dp,
    topPinnedHeight: Dp,
    bottomPinnedHeight: Dp,
    scrollBehavior: TopAppBarScrollBehavior?
) {
    if (maxHeight <= topPinnedHeight) {
        throw IllegalArgumentException(
            "A TwoRowsTopAppBar max height should be greater than its pinned height"
        )
    }

    val topPinnedHeightPx: Float
    val bottomPinnedHeightPx: Float
    val maxHeightPx: Float
    val titleBottomPaddingPx: Int
    LocalDensity.current.run {
        topPinnedHeightPx = topPinnedHeight.toPx()
        bottomPinnedHeightPx = bottomPinnedHeight.toPx()
        maxHeightPx = maxHeight.toPx()
        titleBottomPaddingPx = titleBottomPadding.roundToPx()
    }
    val slideHeightPx = (maxHeightPx
            - bottomPinnedHeightPx
            - titleBottomPaddingPx
            + (scrollBehavior?.state?.heightOffset ?: 0f))
    val imageBannerHeightPx = slideHeightPx + titleBottomPaddingPx

    // Sets the app bar's height offset limit to hide just the bottom title area and keep top title
    // visible when collapsed.
    SideEffect {
        val newHeightOffsetLimit = -maxHeightPx
        if (scrollBehavior?.state?.heightOffsetLimit != newHeightOffsetLimit) {
            scrollBehavior?.state?.heightOffsetLimit = newHeightOffsetLimit
        }
    }

    // Obtain the container Color from the TopAppBarWithImageColors using the `collapsedFraction`, as the
    // bottom part of this TwoRowsTopAppBar changes color at the same rate the app bar expands or
    // collapse.
    // This will potentially animate or interpolate a transition between the container color and the
    // container's scrolled color according to the app bar's scroll state.
    val colorTransitionFraction = scrollBehavior?.state?.collapsedFraction ?: 0f
    val appBarContainerColor by rememberUpdatedState(colors.containerColor(colorTransitionFraction))
    val imageOverlayColor by rememberUpdatedState(colors.imageOverlayColor(colorTransitionFraction))

    // Wrap the given actions in a Row.
    val actionsRow = @Composable {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }
    val titleAlpha = 1f - colorTransitionFraction
    // Hide the top row title semantics when its alpha value goes below 0.5 threshold.
    // Hide the bottom row title semantics when the top title semantics are active.
    val hideTopRowSemantics = colorTransitionFraction < 0.5f
    val hideBottomRowSemantics = !hideTopRowSemantics

    // Set up support for resizing the top app bar when vertically dragging the bar itself.
    val appBarDragModifier = if (scrollBehavior != null && !scrollBehavior.isPinned) {
        Modifier.draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffset + delta
            },
            onDragStopped = { snapTopAppBar(scrollBehavior.state) }
        )
    } else {
        Modifier
    }

    Surface(
        modifier = modifier.then(appBarDragModifier),
        color = appBarContainerColor,
    ) {
        Column(
            Modifier
                .windowInsetsPadding(windowInsets)
                // clip after padding so we don't know the title over the inset area
                .clipToBounds()
        ) {
            TopAppBarLayout(
                modifier = Modifier,
                heightPx = topPinnedHeightPx,
                imageBannerHeightPx = imageBannerHeightPx,
                navigationIconContentColor = colors.navigationIconContentColor,
                titleContentColor = colors.titleContentColor,
                actionIconContentColor = colors.actionIconContentColor,
                title = smallTitle,
                titleTextStyle = smallTitleTextStyle,
                titleAlpha = 1f - titleAlpha,
                titleVerticalArrangement = Arrangement.Center,
                titleHorizontalArrangement = Arrangement.Start,
                titleBottomPadding = 0,
                hideTitleSemantics = hideTopRowSemantics,
                navigationIcon = navigationIcon,
                actions = actionsRow,
                imageBanner = {
                    ImageBanner(
                        url = imageUrl,
                        imageDescription = null,
                        overlayColor = imageOverlayColor,
                    )
                },
            )
            TopAppBarLayout(
                modifier = Modifier.clipToBounds(),
                heightPx = null,
                imageBannerHeightPx = slideHeightPx,
                navigationIconContentColor = colors.navigationIconContentColor,
                titleContentColor = colors.titleContentColor,
                actionIconContentColor = colors.actionIconContentColor,
                title = title,
                titleTextStyle = titleTextStyle,
                titleAlpha = titleAlpha,
                titleVerticalArrangement = Arrangement.Bottom,
                titleHorizontalArrangement = Arrangement.Start,
                titleBottomPadding = titleBottomPaddingPx,
                hideTitleSemantics = hideBottomRowSemantics,
                navigationIcon = {},
                actions = {},
                imageBanner = {},
            )
        }
    }
}

@Composable
private fun ImageBanner(
    url: String?,
    imageDescription: String?,
    overlayColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (url == null) {
            Placeholder(isHighlightVisible = true)
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                loading = {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Success) {
                        SubcomposeAsyncImageContent()
                    } else {
                        Placeholder(
                            isHighlightVisible = state !is AsyncImagePainter.State.Error,
                        )
                    }
                },
                error = {
                    Placeholder(isHighlightVisible = false)
                },
                onError = {
                    Log.d(TAG, "Unable to load image $url", it.result.throwable)
                },
                contentDescription = imageDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Surface(
            color = overlayColor,
            modifier = Modifier.fillMaxSize(),
        ) {}
    }
}

@Composable
private fun Placeholder(
    isHighlightVisible: Boolean,
) {
    Box(
        modifier = Modifier
            .placeholder(
                visible = isHighlightVisible,
                shape = RectangleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                highlight = PlaceholderHighlight.fade()
            )
            .fillMaxSize(),
    ) {}
}

@OptIn(ExperimentalMaterial3Api::class)
private suspend fun snapTopAppBar(state: TopAppBarState) {
    // In case the app bar motion was stopped in a state where it's partially visible, snap it to
    // the nearest state.
    if (state.heightOffset < 0 &&
        state.heightOffset > state.heightOffsetLimit
    ) {
        AnimationState(initialValue = state.heightOffset).animateTo(
            if (state.collapsedFraction < 0.5f) 0f else state.heightOffsetLimit,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        ) { state.heightOffset = value }
    }
}

@Composable
private fun TopAppBarLayout(
    modifier: Modifier,
    heightPx: Float?,
    imageBannerHeightPx: Float,
    navigationIconContentColor: Color,
    titleContentColor: Color,
    actionIconContentColor: Color,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    titleAlpha: Float,
    titleVerticalArrangement: Arrangement.Vertical,
    titleHorizontalArrangement: Arrangement.Horizontal,
    titleBottomPadding: Int,
    hideTitleSemantics: Boolean,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    imageBanner: @Composable () -> Unit,
) {
    Layout(
        {
            Box(
                Modifier.layoutId("imageBanner")
            ) {
                imageBanner()
            }
            Box(
                Modifier
                    .layoutId("navigationIcon")
                    .padding(start = TopAppBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides navigationIconContentColor,
                    content = navigationIcon
                )
            }
            Box(
                Modifier
                    .layoutId("title")
                    .padding(horizontal = TopAppBarHorizontalPadding)
                    .then(if (hideTitleSemantics) Modifier.clearAndSetSemantics { } else Modifier)
                    .alpha(titleAlpha)
            ) {
                ProvideTextStyle(value = titleTextStyle) {
                    CompositionLocalProvider(
                        LocalContentColor provides titleContentColor,
                        content = title
                    )
                }
            }
            Box(
                Modifier
                    .layoutId("actionIcons")
                    .padding(end = TopAppBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides actionIconContentColor,
                    content = actions
                )
            }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val imageBannerHeight = imageBannerHeightPx.roundToInt()
        val imageBannerPlaceable =
            measurables.first { it.layoutId == "imageBanner" }
                .measure(constraints.copy(minWidth = 0, maxHeight = max(0, imageBannerHeight)))
        val navigationIconPlaceable =
            measurables.first { it.layoutId == "navigationIcon" }
                .measure(constraints.copy(minWidth = 0))
        val actionIconsPlaceable =
            measurables.first { it.layoutId == "actionIcons" }
                .measure(constraints.copy(minWidth = 0))

        val maxTitleWidth = if (constraints.maxWidth == Constraints.Infinity) {
            constraints.maxWidth
        } else {
            (constraints.maxWidth - navigationIconPlaceable.width - actionIconsPlaceable.width)
                .coerceAtLeast(0)
        }
        val titlePlaceable =
            measurables.first { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

        // Locate the title's baseline.
        val titleBaseline =
            if (titlePlaceable[LastBaseline] != AlignmentLine.Unspecified) {
                titlePlaceable[LastBaseline]
            } else {
                0
            }

        val layoutHeight = heightPx?.roundToInt()
            ?: (imageBannerHeight + titlePlaceable.height)

        layout(constraints.maxWidth, layoutHeight.coerceAtLeast(0)) {
            // Image banner
            imageBannerPlaceable.placeRelative(
                x = 0,
                y = 0,
            )

            // Navigation icon
            navigationIconPlaceable.placeRelative(
                x = 0,
                y = (layoutHeight - navigationIconPlaceable.height) / 2
            )

            // Title
            titlePlaceable.placeRelative(
                x = when (titleHorizontalArrangement) {
                    Arrangement.Center -> (constraints.maxWidth - titlePlaceable.width) / 2
                    Arrangement.End ->
                        constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width
                    // Arrangement.Start.
                    // An TopAppBarTitleInset will make sure the title is offset in case the
                    // navigation icon is missing.
                    else -> max(TopAppBarTitleInset.roundToPx(), navigationIconPlaceable.width)
                },
                y = when (titleVerticalArrangement) {
                    Arrangement.Center -> (layoutHeight - titlePlaceable.height) / 2
                    // Apply bottom padding from the title's baseline only when the Arrangement is
                    // "Bottom".
                    Arrangement.Bottom ->
                        if (titleBottomPadding == 0) layoutHeight - titlePlaceable.height
                        else layoutHeight - titlePlaceable.height - max(
                            0,
                            titleBottomPadding - titlePlaceable.height + titleBaseline
                        )
                    // Arrangement.Top
                    else -> 0
                }
            )

            // Action icons
            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = (layoutHeight - actionIconsPlaceable.height) / 2
            )
        }
    }
}

@Stable
class TopAppBarWithImageColors internal constructor(
    private val containerColor: Color,
    private val scrolledContainerColor: Color,
    internal val navigationIconContentColor: Color,
    internal val titleContentColor: Color,
    internal val actionIconContentColor: Color,
) {

    /**
     * Represents the container color used for the top app bar.
     *
     * A [colorTransitionFraction] provides a percentage value that can be used to generate a color.
     * Usually, an app bar implementation will pass in a [colorTransitionFraction] read from
     * the [TopAppBarState.collapsedFraction] or the [TopAppBarState.overlappedFraction].
     *
     * @param colorTransitionFraction a `0.0` to `1.0` value that represents a color transition
     * percentage
     */
    @Composable
    internal fun containerColor(colorTransitionFraction: Float): Color {
        return lerp(
            containerColor,
            scrolledContainerColor.copy(alpha = colorTransitionFraction),
            FastOutLinearInEasing.transform(colorTransitionFraction)
        )
    }

    @Composable
    internal fun imageOverlayColor(colorTransitionFraction: Float): Color {
        return scrolledContainerColor.copy(alpha = colorTransitionFraction)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is TopAppBarWithImageColors) return false

        if (containerColor != other.containerColor) return false
        if (scrolledContainerColor != other.scrolledContainerColor) return false
        if (navigationIconContentColor != other.navigationIconContentColor) return false
        if (titleContentColor != other.titleContentColor) return false
        if (actionIconContentColor != other.actionIconContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + scrolledContainerColor.hashCode()
        result = 31 * result + navigationIconContentColor.hashCode()
        result = 31 * result + titleContentColor.hashCode()
        result = 31 * result + actionIconContentColor.hashCode()

        return result
    }
}

object TopAppBarWithImageDefaults {
    @Composable
    fun topAppBarColors(
        containerColor: Color = MaterialTheme.colorScheme.surface,
        scrolledContainerColor: Color = MaterialTheme.colorScheme.applyTonalElevation(
            backgroundColor = containerColor,
            elevation = 3.0.dp,
        ),
        navigationIconContentColor: Color = MaterialTheme.colorScheme.onSurface,
        titleContentColor: Color = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ): TopAppBarWithImageColors =
        TopAppBarWithImageColors(
            containerColor,
            scrolledContainerColor,
            navigationIconContentColor,
            titleContentColor,
            actionIconContentColor
        )
}

private val TopAppBarBottomPadding = 28.dp
private val TopAppBarHorizontalPadding = 4.dp
private val TopAppBarPinnedHeight = 64.0.dp
private val TopAppBarMaxHeight = 256.0.dp

// A title inset when the App-Bar is a Medium or Large one. Also used to size a spacer when the
// navigation icon is missing.
private val TopAppBarTitleInset = 16.dp - TopAppBarHorizontalPadding

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun PreviewTopAppBarWithImage() {
    val scrollBehavior = rememberTopAppBarWithImageScrollBehavior()

    OpenComicVineTheme {
        Scaffold(
            topBar = {
                TopAppBarWithImage(
                    title = { Text("Title") },
                    imageUrl = "https://comicvine.gamespot.com/a/uploads/screen_kubrick/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                    navigationIcon = { FilledTonalBackButton(onClick = {}) },
                    actions = {
                        FilledTonalActionButton(onClick = {}) {
                            Icon(
                                painterResource(R.drawable.ic_favorite_24),
                                contentDescription = ""
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {}
    }
}

@Preview(name = "Without image")
@Composable
private fun PreviewTopAppBarWithImage_WithoutImage() {
    OpenComicVineTheme {
        TopAppBarWithImage(
            title = { Text("Title") },
            imageUrl = null,
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Large title", showSystemUi = true, showBackground = true)
@Composable
private fun PreviewTopAppBarWithImage_LargeTitle() {
    val scrollBehavior = rememberTopAppBarWithImageScrollBehavior()

    OpenComicVineTheme {
        Scaffold(
            topBar = {
                val maxLines = 3
                TopAppBarWithImage(
                    title = {
                        Text(
                            "Multi\nline\ntitle",
                            maxLines = maxLines,
                        )
                    },
                    smallTitle = {
                        Text("Multi line title")
                    },
                    imageUrl = "https://comicvine.gamespot.com/a/uploads/screen_kubrick/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                    scrollBehavior = scrollBehavior,
                    maxBottomPinnedHeight = TopAppBarWithImageTitleStyle.calculateTextHeight(
                        maxLines = maxLines,
                    ),
                )
            },
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {}
    }
}