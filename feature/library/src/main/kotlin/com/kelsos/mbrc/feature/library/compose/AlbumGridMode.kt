package com.kelsos.mbrc.feature.library.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import com.kelsos.mbrc.core.common.settings.AlbumViewMode

/**
 * Whether [viewMode] lays albums out as a grid in the current window, which decides
 * [AlbumViewMode.AUTO] by the window width.
 */
@Composable
internal fun isAlbumGrid(viewMode: AlbumViewMode): Boolean {
  val widthDp = with(LocalDensity.current) {
    LocalWindowInfo.current.containerSize.width.toDp()
  }
  return viewMode.isGrid(widthDp.value.toInt())
}
