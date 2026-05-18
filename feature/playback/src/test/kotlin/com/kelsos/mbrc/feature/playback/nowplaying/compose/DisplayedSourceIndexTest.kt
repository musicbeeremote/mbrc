package com.kelsos.mbrc.feature.playback.nowplaying.compose

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.ui.compose.displayedSourceIndex
import org.junit.Test

/**
 * Verifies the in-flight drag permutation: given that the dragged item started
 * at `source` and currently sits at `target`, what should each LazyColumn slot
 * render? The function maps slot → source-list index without mutating any
 * underlying data; the rest of the list shifts by one slot toward `source`.
 */
class DisplayedSourceIndexTest {

  @Test
  fun `no drag in progress passes the slot through unchanged`() {
    for (slot in 0..10) {
      assertThat(displayedSourceIndex(slot, source = null, target = null)).isEqualTo(slot)
      assertThat(displayedSourceIndex(slot, source = 5, target = null)).isEqualTo(slot)
      assertThat(displayedSourceIndex(slot, source = null, target = 5)).isEqualTo(slot)
    }
  }

  @Test
  fun `dragged-onto-itself passes the slot through unchanged`() {
    for (slot in 0..10) {
      assertThat(displayedSourceIndex(slot, source = 5, target = 5)).isEqualTo(slot)
    }
  }

  @Test
  fun `moving down shifts intermediate slots up by one and renders source at target`() {
    // Source list: [A B C D E F]; user drags B (index 1) down onto E (index 4).
    // Expected displayed order: [A C D E B F].
    // Slot 0 -> A (0), slot 1 -> C (2), slot 2 -> D (3), slot 3 -> E (4),
    // slot 4 -> B (1, the dragged item), slot 5 -> F (5).
    val src = 1
    val tgt = 4
    assertThat(displayedSourceIndex(0, src, tgt)).isEqualTo(0)
    assertThat(displayedSourceIndex(1, src, tgt)).isEqualTo(2)
    assertThat(displayedSourceIndex(2, src, tgt)).isEqualTo(3)
    assertThat(displayedSourceIndex(3, src, tgt)).isEqualTo(4)
    assertThat(displayedSourceIndex(4, src, tgt)).isEqualTo(1)
    assertThat(displayedSourceIndex(5, src, tgt)).isEqualTo(5)
  }

  @Test
  fun `moving up shifts intermediate slots down by one and renders source at target`() {
    // Source list: [A B C D E F]; user drags E (index 4) up onto B (index 1).
    // Expected displayed order: [A E B C D F].
    val src = 4
    val tgt = 1
    assertThat(displayedSourceIndex(0, src, tgt)).isEqualTo(0)
    assertThat(displayedSourceIndex(1, src, tgt)).isEqualTo(4)
    assertThat(displayedSourceIndex(2, src, tgt)).isEqualTo(1)
    assertThat(displayedSourceIndex(3, src, tgt)).isEqualTo(2)
    assertThat(displayedSourceIndex(4, src, tgt)).isEqualTo(3)
    assertThat(displayedSourceIndex(5, src, tgt)).isEqualTo(5)
  }

  @Test
  fun `single-step move is the trivial swap`() {
    // Down by one: drag from 2 to 3.
    assertThat(displayedSourceIndex(2, source = 2, target = 3)).isEqualTo(3)
    assertThat(displayedSourceIndex(3, source = 2, target = 3)).isEqualTo(2)

    // Up by one: drag from 3 to 2.
    assertThat(displayedSourceIndex(2, source = 3, target = 2)).isEqualTo(3)
    assertThat(displayedSourceIndex(3, source = 3, target = 2)).isEqualTo(2)
  }

  @Test
  fun `slots well outside the drag range are untouched`() {
    val src = 100
    val tgt = 110
    for (slot in listOf(0, 50, 99, 111, 200, 1000)) {
      assertThat(displayedSourceIndex(slot, src, tgt)).isEqualTo(slot)
    }
  }

  @Test
  fun `permutation is a bijection over the affected range`() {
    // Move down from 2 to 7: every slot in [0, 10) must map to a unique
    // source index in [0, 10) — no item shown twice, no item dropped.
    val src = 2
    val tgt = 7
    val mapped = (0 until 10).map { displayedSourceIndex(it, src, tgt) }
    assertThat(mapped.toSet()).isEqualTo((0 until 10).toSet())

    // Same property when moving up.
    val mappedUp = (0 until 10).map { displayedSourceIndex(it, source = 7, target = 2) }
    assertThat(mappedUp.toSet()).isEqualTo((0 until 10).toSet())
  }
}
