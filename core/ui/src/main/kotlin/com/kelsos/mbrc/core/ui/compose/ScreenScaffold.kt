package com.kelsos.mbrc.core.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * A reusable scaffold composable for screens.
 * Each screen wraps its content with this scaffold to have consistent top bar, FAB, and snackbar.
 *
 * @param title The title to display in the top bar
 * @param snackbarHostState The snackbar host state for showing snackbars
 * @param modifier Modifier for the scaffold
 * @param navigationIcon The type of navigation icon (drawer, back, or none)
 * @param onOpenDrawer Callback when the drawer icon is clicked
 * @param actionItems Action icon buttons shown before the overflow menu
 * @param menuItems Menu items for the overflow menu
 * @param floatingActionButton Optional FAB composable
 * @param isTransparent Whether the top bar should be transparent
 * @param content The screen content
 */
@Composable
fun ScreenScaffold(
  title: String,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  navigationIcon: NavigationIconType = NavigationIconType.Drawer,
  onOpenDrawer: () -> Unit = {},
  actionItems: List<ActionItem> = emptyList(),
  menuItems: List<MenuItem> = emptyList(),
  floatingActionButton: @Composable () -> Unit = {},
  isTransparent: Boolean = false,
  content: @Composable (PaddingValues) -> Unit
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      ScaffoldTopBar(
        state = TopBarState.WithTitle(title),
        navigationIcon = navigationIcon,
        actionItems = actionItems,
        menuItems = menuItems,
        isTransparent = isTransparent,
        defaultTitle = title,
        onOpenDrawer = onOpenDrawer
      )
    },
    floatingActionButton = floatingActionButton,
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    containerColor = if (isTransparent) {
      Color.Transparent
    } else {
      MaterialTheme.colorScheme.background
    },
    content = content
  )
}

/**
 * A scaffold variant that supports dynamic top bar states (search, progress, hidden, etc.).
 * Use this when your screen needs more complex top bar behavior.
 *
 * @param topBarState The current state of the top bar
 * @param snackbarHostState The snackbar host state for showing snackbars
 * @param modifier Modifier for the scaffold
 * @param defaultTitle The default title to show when topBarState is Default
 * @param navigationIcon The type of navigation icon (drawer, back, or none)
 * @param onOpenDrawer Callback when the drawer icon is clicked
 * @param actionItems Action icon buttons shown before the overflow menu
 * @param menuItems Menu items for the overflow menu
 * @param onOverflowClick Optional callback that overrides the menu dropdown (opens bottom sheet, etc.)
 * @param fabState The FAB state
 * @param isTransparent Whether the top bar should be transparent
 * @param content The screen content
 */
@Composable
fun DynamicScreenScaffold(
  topBarState: TopBarState,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  defaultTitle: String = "",
  navigationIcon: NavigationIconType = NavigationIconType.Drawer,
  onOpenDrawer: () -> Unit = {},
  actionItems: List<ActionItem> = emptyList(),
  menuItems: List<MenuItem> = emptyList(),
  onOverflowClick: (() -> Unit)? = null,
  fabState: FabState = FabState.Hidden,
  isTransparent: Boolean = false,
  content: @Composable (PaddingValues) -> Unit
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      if (topBarState != TopBarState.Hidden) {
        ScaffoldTopBar(
          state = topBarState,
          navigationIcon = navigationIcon,
          actionItems = actionItems,
          menuItems = menuItems,
          isTransparent = isTransparent,
          defaultTitle = defaultTitle,
          onOpenDrawer = onOpenDrawer,
          onOverflowClick = onOverflowClick
        )
      }
    },
    floatingActionButton = { ScaffoldFab(state = fabState) },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    containerColor = if (isTransparent) {
      Color.Transparent
    } else {
      MaterialTheme.colorScheme.background
    },
    content = content
  )
}
