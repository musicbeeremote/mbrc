package com.kelsos.mbrc.core.common.settings

import com.kelsos.mbrc.core.common.layout.WindowWidthClass

enum class AlbumViewMode(val value: String) {
  LIST("list"),
  GRID("grid"),
  AUTO("auto");

  fun isGrid(screenWidthDp: Int): Boolean = when (this) {
    AUTO -> WindowWidthClass.fromWidthDp(screenWidthDp) != WindowWidthClass.Compact
    GRID -> true
    LIST -> false
  }

  /**
   * The mode the view mode toggle switches to. [AUTO] resolves to [LIST] because the toggle only
   * offers list and grid, and leaving automatic is the point of pressing it.
   */
  fun next(): AlbumViewMode = when (this) {
    AUTO -> LIST
    LIST -> GRID
    GRID -> LIST
  }

  companion object {
    fun fromString(value: String): AlbumViewMode = entries.find { it.value == value } ?: AUTO
  }
}
