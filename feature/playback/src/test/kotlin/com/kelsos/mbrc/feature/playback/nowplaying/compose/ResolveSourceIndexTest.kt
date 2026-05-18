package com.kelsos.mbrc.feature.playback.nowplaying.compose

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResolveSourceIndexTest {

  private val noIdAtTarget: (Int) -> Long? = { null }

  @Test
  fun `no drag - source null - returns slot unchanged`() {
    val result = resolveSourceIndex(
      slot = 3,
      source = null,
      target = 5,
      isSettling = false,
      draggedId = 99L,
      idAtTarget = noIdAtTarget
    )
    assertThat(result).isEqualTo(3)
  }

  @Test
  fun `no drag - target null - returns slot unchanged`() {
    val result = resolveSourceIndex(
      slot = 3,
      source = 5,
      target = null,
      isSettling = true,
      draggedId = 99L,
      idAtTarget = { 99L }
    )
    assertThat(result).isEqualTo(3)
  }

  @Test
  fun `active drag - applies permutation`() {
    // Drag from 1 to 4: slot 4 should display the source item (index 1).
    val result = resolveSourceIndex(
      slot = 4,
      source = 1,
      target = 4,
      isSettling = false,
      draggedId = 42L,
      idAtTarget = noIdAtTarget
    )
    assertThat(result).isEqualTo(1)
  }

  @Test
  fun `settling but paging has not caught up - keeps applying permutation`() {
    // Mid-settle: idAtTarget still returns a stale id (the neighbor that
    // was originally at target). Permutation must hold to avoid snap-back.
    val staleNeighborId = 7L
    val draggedId = 42L
    val result = resolveSourceIndex(
      slot = 4,
      source = 1,
      target = 4,
      isSettling = true,
      draggedId = draggedId,
      idAtTarget = { staleNeighborId }
    )
    assertThat(result).isEqualTo(1)
  }

  @Test
  fun `settling and paging emitted reorder - passes slot through`() {
    // Paging has caught up: idAtTarget now returns the dragged item's id,
    // so the permutation is no longer needed. Slot maps straight through;
    // the LazyColumn renders the persisted order directly.
    val draggedId = 42L
    val result = resolveSourceIndex(
      slot = 4,
      source = 1,
      target = 4,
      isSettling = true,
      draggedId = draggedId,
      idAtTarget = { draggedId }
    )
    assertThat(result).isEqualTo(4)
  }

  @Test
  fun `settling with null draggedId - falls back to permutation`() {
    // draggedId is null when the source slot was a placeholder at drag-start
    // (peek returned null). We can't detect the paging emission without an
    // id to compare against, so we hold the permutation until the SETTLE
    // timeout clears the drag state.
    val result = resolveSourceIndex(
      slot = 4,
      source = 1,
      target = 4,
      isSettling = true,
      draggedId = null,
      idAtTarget = { 42L }
    )
    assertThat(result).isEqualTo(1)
  }

  @Test
  fun `idAtTarget is only consulted while settling`() {
    var calls = 0
    val countingIdAtTarget: (Int) -> Long? = {
      calls += 1
      42L
    }
    resolveSourceIndex(
      slot = 4,
      source = 1,
      target = 4,
      isSettling = false,
      draggedId = 42L,
      idAtTarget = countingIdAtTarget
    )
    assertThat(calls).isEqualTo(0)
  }

  @Test
  fun `slots outside the drag range pass through during active drag`() {
    val result = resolveSourceIndex(
      slot = 50,
      source = 1,
      target = 4,
      isSettling = false,
      draggedId = 42L,
      idAtTarget = noIdAtTarget
    )
    assertThat(result).isEqualTo(50)
  }
}
