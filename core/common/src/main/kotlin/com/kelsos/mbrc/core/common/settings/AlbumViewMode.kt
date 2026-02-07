package com.kelsos.mbrc.core.common.settings

private const val TABLET_WIDTH_DP = 600

enum class AlbumViewMode(val value: String) {
  LIST("list"),
  GRID("grid"),
  AUTO("auto");

  fun isGrid(screenWidthDp: Int): Boolean = when (this) {
    AUTO -> screenWidthDp >= TABLET_WIDTH_DP
    GRID -> true
    LIST -> false
  }

  companion object {
    fun fromString(value: String): AlbumViewMode = entries.find { it.value == value } ?: AUTO
  }
}
