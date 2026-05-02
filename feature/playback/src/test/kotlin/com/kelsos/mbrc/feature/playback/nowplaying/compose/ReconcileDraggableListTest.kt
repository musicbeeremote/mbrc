package com.kelsos.mbrc.feature.playback.nowplaying.compose

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.data.nowplaying.NowPlaying
import org.junit.Test

class ReconcileDraggableListTest {

  private fun track(id: Long, position: Int = id.toInt()) = NowPlaying(
    id = id,
    title = "Track $id",
    artist = "Artist $id",
    path = "/path/$id",
    position = position
  )

  @Test
  fun `idle - replaces local list with incoming when content differs`() {
    val current = mutableListOf(track(1), track(2), track(3))
    val incoming = listOf(track(1), track(3), track(2))

    reconcileDraggableList(current = current, incoming = incoming, isDragging = false)

    assertThat(current.map { it.id }).containsExactly(1L, 3L, 2L).inOrder()
  }

  @Test
  fun `idle - leaves list untouched when content is identical`() {
    val items = listOf(track(1), track(2), track(3))
    val current = items.toMutableList()
    val incoming = items.toList()

    reconcileDraggableList(current = current, incoming = incoming, isDragging = false)

    assertThat(current).isEqualTo(items)
  }

  @Test
  fun `dragging - preserves local order when a new page lands`() {
    // User reordered locally: 1, 3, 2 (originally 1, 2, 3)
    val current = mutableListOf(track(1), track(3), track(2))
    // Server hasn't seen the reorder yet; new page appends ids 4, 5.
    val incoming = listOf(track(1), track(2), track(3), track(4), track(5))

    reconcileDraggableList(current = current, incoming = incoming, isDragging = true)

    assertThat(current.map { it.id }).containsExactly(1L, 3L, 2L, 4L, 5L).inOrder()
  }

  @Test
  fun `dragging - does nothing when no new ids arrive`() {
    val current = mutableListOf(track(1), track(3), track(2))
    val incoming = listOf(track(1), track(2), track(3))

    reconcileDraggableList(current = current, incoming = incoming, isDragging = true)

    assertThat(current.map { it.id }).containsExactly(1L, 3L, 2L).inOrder()
  }

  @Test
  fun `dragging - allows reordering across the page border once new items arrive`() {
    // Initial loaded page: ids 1..3, user is dragging.
    val current = mutableListOf(track(1), track(2), track(3))
    // Page boundary load brings in ids 4, 5.
    val incoming = listOf(track(1), track(2), track(3), track(4), track(5))

    reconcileDraggableList(current = current, incoming = incoming, isDragging = true)
    assertThat(current.map { it.id }).containsExactly(1L, 2L, 3L, 4L, 5L).inOrder()

    // User now drags id 1 past the original page boundary, between 4 and 5.
    val from = current.indexOfFirst { it.id == 1L }
    current.add(3, current.removeAt(from))

    // Mid-drag emission must not snap the dragged item back to the server order.
    val secondIncoming = listOf(track(1), track(2), track(3), track(4), track(5))
    reconcileDraggableList(current = current, incoming = secondIncoming, isDragging = true)

    assertThat(current.map { it.id }).containsExactly(2L, 3L, 4L, 1L, 5L).inOrder()
  }

  @Test
  fun `dragging then idle - drag-end reconciles to server state`() {
    val current = mutableListOf(track(1), track(3), track(2))

    reconcileDraggableList(
      current = current,
      incoming = listOf(track(1), track(2), track(3)),
      isDragging = true
    )
    assertThat(current.map { it.id }).containsExactly(1L, 3L, 2L).inOrder()

    reconcileDraggableList(
      current = current,
      incoming = listOf(track(1), track(3), track(2)),
      isDragging = false
    )
    assertThat(current.map { it.id }).containsExactly(1L, 3L, 2L).inOrder()
  }
}
