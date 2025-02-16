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

package org.proninyaroslav.opencomicvine.ui.components.drawer

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.chip.ChipFlowRow
import org.proninyaroslav.opencomicvine.ui.formatDateRange
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.util.*

private val DrawerPadding = 28.dp
private val ExtendedFabHeight = 56.dp

enum class AdaptiveFilterDrawerType {
    Modal,
    Permanent,
}

@Composable
fun AdaptiveFilterDrawer(
    modifier: Modifier = Modifier,
    isExpandedWidth: Boolean,
    drawerContent: LazyListScope.() -> Unit,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    showApplyButton: Boolean = false,
    onClose: () -> Unit,
    onApply: () -> Unit,
    onTypeChanged: (AdaptiveFilterDrawerType) -> Unit = {},
    content: @Composable () -> Unit,
) {
    BoxWithConstraints {
        val drawerType by remember {
            derivedStateOf {
                if (isExpandedWidth && maxWidth >= DrawerDefaults.MaximumDrawerWidth * 3) {
                    AdaptiveFilterDrawerType.Permanent
                } else {
                    AdaptiveFilterDrawerType.Modal
                }.also { onTypeChanged(it) }
            }
        }

        when (drawerType) {
            AdaptiveFilterDrawerType.Modal -> FilterDrawer(
                drawerContent = drawerContent,
                showApplyButton = showApplyButton,
                drawerState = drawerState,
                onClose = onClose,
                onApply = onApply,
                modifier = modifier,
                content = content,
            )

            AdaptiveFilterDrawerType.Permanent -> FilterPermanentDrawer(
                drawerContent = drawerContent,
                showApplyButton = showApplyButton,
                onApply = onApply,
                modifier = modifier,
                content = content,
            )
        }
    }
}

@Composable
fun FilterDrawer(
    modifier: Modifier = Modifier,
    drawerContent: LazyListScope.() -> Unit,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    showApplyButton: Boolean = false,
    onClose: () -> Unit,
    onApply: () -> Unit,
    content: @Composable () -> Unit,
) {
    CustomModalNavigationDrawer(
        drawerContent = {
            CustomModalDrawerSheet(
                floatingActionButton = {
                    ApplyButton(
                        showApplyButton = showApplyButton,
                        onApply = onApply,
                    )
                },
            ) {
                item { Header(onClose = onClose) }
                drawerContent()
                item { ApplyButtonSpacer() }
            }
        },
        drawerState = drawerState,
        direction = LayoutDirection.Rtl,
        modifier = modifier,
        content = content,
    )
}

@Composable
fun FilterPermanentDrawer(
    modifier: Modifier = Modifier,
    drawerContent: LazyListScope.() -> Unit,
    showApplyButton: Boolean = false,
    onApply: () -> Unit,
    content: @Composable () -> Unit,
) {
    CustomPermanentNavigationDrawer(
        drawerContent = {
            CustomPermanentDrawerSheet(
                floatingActionButton = {
                    ApplyButton(
                        showApplyButton = showApplyButton,
                        onApply = onApply,
                    )
                },
            ) {
                drawerContent()
                item { ApplyButtonSpacer() }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .sizeIn(maxWidth = DividerDefaults.Thickness)
            )
        },
        direction = LayoutDirection.Rtl,
        modifier = modifier,
        content = content,
    )
}

@Composable
private fun BoxScope.ApplyButton(showApplyButton: Boolean, onApply: () -> Unit) {
    ApplyButton(
        visible = showApplyButton,
        onClick = onApply,
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomEnd),
    )
}

@Composable
private fun ApplyButtonSpacer() {
    Spacer(modifier = Modifier.height(ExtendedFabHeight + 32.dp))
}

@Composable
private fun Header(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(
            horizontal = DrawerPadding,
            vertical = 16.dp,
        ),
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier.padding(end = 12.dp),
        ) {
            Icon(
                painterResource(R.drawable.ic_clear_24),
                contentDescription = stringResource(R.string.close),
            )
        }
        Text(
            stringResource(R.string.filter),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
fun FilterRadioButtonItem(
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationDrawerItem(
        label = label,
        icon = {
            RadioButton(
                selected = selected,
                onClick = onClick,
            )
        },
        selected = false,
        onClick = onClick,
        modifier = modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun FilterTextFieldItem(
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = {},
    value: String,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onValueChanged: (String) -> Unit,
) {
    TextField(
        value = value,
        placeholder = placeholder,
        enabled = enabled,
        readOnly = readOnly,
        interactionSource = interactionSource,
        onValueChange = onValueChanged,
        modifier = modifier
            .padding(horizontal = 40.dp, vertical = 8.dp)
            .heightIn(min = 56.0.dp)
            .fillMaxWidth()
    )
}

@Composable
fun FilterDatePickerItem(
    modifier: Modifier = Modifier,
    value: Pair<Date, Date>,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val currentOnClick by rememberUpdatedState(onClick)
    LaunchedEffect(isPressed) {
        if (isPressed) {
            currentOnClick()
        }
    }
    FilterTextFieldItem(
        value = value.run { formatDateRange(start = first, end = second) },
        enabled = enabled,
        readOnly = true,
        interactionSource = source,
        onValueChanged = {},
        modifier = modifier,
    )
}

@Composable
fun FilterSectionHeader(
    title: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(
            horizontal = DrawerPadding + 16.dp,
            vertical = 8.dp,
        ),
    ) {
        Icon(
            painterResource(icon),
            contentDescription = title,
            modifier = Modifier.padding(end = 20.dp),
        )
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
fun FilterSelectableChipItem(
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    FilterChip(
        label = label,
        selected = selected,
        onClick = onClick,
        leadingIcon = if (selected) {
            {
                SelectableChipItemIcon()
            }
        } else {
            leadingIcon
        },
        modifier = modifier,
    )
}

@Composable
fun FilterElevatedSelectableChipItem(
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    ElevatedFilterChip(
        label = label,
        selected = selected,
        onClick = onClick,
        leadingIcon = if (selected) {
            {
                SelectableChipItemIcon()
            }
        } else {
            leadingIcon
        },
        elevation = FilterChipDefaults.elevatedFilterChipElevation(
            elevation = 3.dp,
        ),
        modifier = modifier,
    )
}

@Composable
fun SelectableChipItemIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        painterResource(R.drawable.ic_done_24),
        contentDescription = stringResource(R.string.selected),
        modifier = modifier.size(FilterChipDefaults.IconSize)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChipList(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(4.dp),
    content: @Composable FlowRowScope.() -> Unit
) {
    ChipFlowRow(
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        modifier = modifier.padding(start = 40.dp),
        content = content,
    )
}

@Composable
private fun ApplyButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(),
        exit = scaleOut(),
        modifier = modifier,
    ) {
        ExtendedFloatingActionButton(onClick = onClick) {
            Icon(
                painterResource(R.drawable.ic_done_24),
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(stringResource(R.string.apply))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun PreviewFilterDrawer() {
    OpenComicVineTheme {
        val chipSelected = remember { mutableStateListOf(false, false, true) }
        FilterDrawer(
            drawerContent = {
                item {
                    FilterSectionHeader(
                        title = "Sort",
                        icon = R.drawable.ic_sort_24,
                    )
                }
                item {
                    FilterRadioButtonItem(
                        label = { Text("Alphabetical") },
                        selected = true,
                        onClick = {}
                    )
                }
                item {
                    FilterSectionHeader(
                        title = "Name",
                        icon = R.drawable.ic_abc_24,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
                item {
                    FilterTextFieldItem(
                        placeholder = { Text("Name contains") },
                        value = "",
                        onValueChanged = {},
                    )
                }
                item {
                    FilterSectionHeader(
                        title = "Type",
                        icon = R.drawable.ic_abc_24,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
                item {
                    FilterChipList {
                        FilterElevatedSelectableChipItem(
                            label = { Text("All") },
                            selected = chipSelected[0],
                            onClick = { chipSelected[0] = !chipSelected[0] },
                        )
                        FilterSelectableChipItem(
                            label = { Text("Characters") },
                            selected = chipSelected[1],
                            onClick = { chipSelected[1] = !chipSelected[1] },
                        )
                        FilterSelectableChipItem(
                            label = { Text("Issues") },
                            selected = chipSelected[2],
                            onClick = { chipSelected[2] = !chipSelected[2] },
                        )
                    }
                }
            },
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            showApplyButton = true,
            onClose = {},
            onApply = {},
        ) {
            Scaffold {}
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun PreviewFilterPermanentDrawer() {
    OpenComicVineTheme {
        val chipSelected = remember { mutableStateListOf(false, false, true) }
        FilterPermanentDrawer(
            drawerContent = {
                item {
                    FilterSectionHeader(
                        title = "Sort",
                        icon = R.drawable.ic_sort_24,
                    )
                }
                item {
                    FilterRadioButtonItem(
                        label = { Text("Alphabetical") },
                        selected = true,
                        onClick = {}
                    )
                }
                item {
                    FilterSectionHeader(
                        title = "Name",
                        icon = R.drawable.ic_abc_24,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
                item {
                    FilterTextFieldItem(
                        placeholder = { Text("Name contains") },
                        value = "",
                        onValueChanged = {},
                    )
                }
                item {
                    FilterSectionHeader(
                        title = "Type",
                        icon = R.drawable.ic_abc_24,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
                item {
                    FilterChipList {
                        FilterElevatedSelectableChipItem(
                            label = { Text("All") },
                            selected = chipSelected[0],
                            onClick = { chipSelected[0] = !chipSelected[0] },
                        )
                        FilterSelectableChipItem(
                            label = { Text("Characters") },
                            selected = chipSelected[1],
                            onClick = { chipSelected[1] = !chipSelected[1] },
                        )
                        FilterSelectableChipItem(
                            label = { Text("Issues") },
                            selected = chipSelected[2],
                            onClick = { chipSelected[2] = !chipSelected[2] },
                        )
                    }
                }
            },
            showApplyButton = true,
            onApply = {},
        ) {
            Scaffold {}
        }
    }
}
