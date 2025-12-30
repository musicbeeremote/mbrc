package com.kelsos.mbrc.app

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

/**
 * Configuration for screen-level UI elements that should be rendered
 * by the main app scaffold.
 *
 * This follows the single-Scaffold pattern recommended by Google where only the root
 * composable owns a Scaffold, and child screens pass their UI configuration up via callbacks.
 */
data class ScreenConfig(
  /**
   * Custom topBar composable. When provided, the parent scaffold will use this
   * instead of the default RemoteTopAppBar. This allows screens to have complex
   * topBars (with search, tabs, progress indicators) without needing their own Scaffold.
   */
  val topBar: (@Composable () -> Unit)? = null,
  /**
   * Actions to show in the default RemoteTopAppBar. Only used when topBar is null.
   */
  val topBarActions: (@Composable () -> Unit)? = null,
  /**
   * When true, the top bar will be transparent and the content will extend behind it.
   * Useful for screens with custom backgrounds (like player screen with gradient).
   */
  val transparentTopBar: Boolean = false,
  /**
   * Floating action button composable to show in the Scaffold.
   */
  val floatingActionButton: (@Composable () -> Unit)? = null,
  /**
   * Flow of snackbar messages to show. Messages are automatically displayed as they're emitted.
   */
  val snackbarMessages: Flow<String>? = null
) {
  companion object {
    /**
     * Default empty configuration for screens that don't need
     * any special scaffold elements.
     */
    val Empty = ScreenConfig()
  }
}

/**
 * Interface for screens that need to configure the main app scaffold.
 * This allows screens to declare what they need (FAB, snackbar messages, etc.)
 * without managing their own scaffold.
 */
interface ConfigurableScreen {
  @Composable
  fun getScreenConfig(): ScreenConfig
}
