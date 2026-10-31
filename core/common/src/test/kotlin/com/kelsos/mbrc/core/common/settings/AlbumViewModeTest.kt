package com.kelsos.mbrc.core.common.settings

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AlbumViewModeTest {
  @Test
  fun `toggling from auto switches to the list`() {
    assertThat(AlbumViewMode.AUTO.next()).isEqualTo(AlbumViewMode.LIST)
  }

  @Test
  fun `toggling from the list switches to the grid`() {
    assertThat(AlbumViewMode.LIST.next()).isEqualTo(AlbumViewMode.GRID)
  }

  @Test
  fun `toggling from the grid switches to the list`() {
    assertThat(AlbumViewMode.GRID.next()).isEqualTo(AlbumViewMode.LIST)
  }
}
