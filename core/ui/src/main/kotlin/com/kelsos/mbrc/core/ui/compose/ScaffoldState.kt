package com.kelsos.mbrc.core.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed interface representing the different states of the top app bar.
 * Each state defines what UI should be rendered by the parent scaffold.
 */
sealed interface TopBarState {
  /** Default top bar with screen title from navigation */
  data object Default : TopBarState

  /** Hidden top bar (e.g., when showing lyrics in player) */
  data object Hidden : TopBarState

  /** Top bar with custom title */
  data class WithTitle(val title: String) : TopBarState

  /** Search mode top bar with text field */
  data class Search(
    val query: String,
    val placeholder: String,
    val onQueryChange: (String) -> Unit,
    val onSearch: () -> Unit,
    val onClose: () -> Unit
  ) : TopBarState

  /**
   * Top bar with progress indicator below.
   * @param title The title to display
   * @param progress Progress value 0f-1f, or -1f for indeterminate
   * @param progressText Optional text to show below progress bar
   */
  data class WithProgress(
    val title: String,
    val progress: Float,
    val progressText: String? = null
  ) : TopBarState
}

/**
 * Sealed interface representing the different states of the floating action button.
 */
sealed interface FabState {
  /** No FAB displayed */
  data object Hidden : FabState

  /** Single FAB with icon and click action */
  data class Single(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
  ) : FabState

  /** Expandable FAB (speed dial) with multiple items */
  data class Expandable(
    val isExpanded: Boolean,
    val onToggle: () -> Unit,
    val items: List<FabItem>
  ) : FabState
}

/**
 * Item for expandable FAB speed dial menu.
 */
data class FabItem(val icon: ImageVector, val label: String, val onClick: () -> Unit)

/**
 * Menu item for the overflow menu in the top bar.
 * @param label Display text for the menu item
 * @param onClick Action when item is clicked
 * @param trailingContent Optional trailing content (e.g., checkbox, switch)
 */
data class MenuItem(
  val label: String,
  val onClick: () -> Unit,
  val trailingContent: (@Composable () -> Unit)? = null
)

/**
 * Action item for the top bar (icon buttons shown before the overflow menu).
 * @param icon The icon to display
 * @param contentDescription Accessibility description
 * @param onClick Action when clicked
 */
data class ActionItem(
  val icon: ImageVector,
  val contentDescription: String,
  val onClick: () -> Unit
)

/**
 * Sealed interface for the navigation icon type.
 */
sealed interface NavigationIconType {
  /** Hamburger menu icon that opens the drawer */
  data object Drawer : NavigationIconType

  /** Back arrow that navigates back */
  data class Back(val onBack: () -> Unit) : NavigationIconType

  /** No navigation icon */
  data object None : NavigationIconType
}
