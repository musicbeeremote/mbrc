package com.kelsos.mbrc.core.networking.client

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Carries the number of commands that were given up on and never reached the player, so the user
 * can be told the tap was lost.
 *
 * Deliberately not part of [UiMessageQueue]: that is a replay-less shared flow, so a message emitted
 * while no screen is composed is discarded. Commands age out precisely when nothing is on screen,
 * the app sitting in the background with no connection, so the notice has to wait for someone to
 * show it to. Losses accumulate until [acknowledge], which also collapses a burst into one message.
 */
interface DroppedCommandNotice {
  val pending: StateFlow<Int>

  fun record(count: Int)

  fun acknowledge()
}

class DroppedCommandNoticeImpl : DroppedCommandNotice {
  private val state = MutableStateFlow(0)

  override val pending: StateFlow<Int> = state.asStateFlow()

  override fun record(count: Int) {
    if (count <= 0) {
      return
    }
    state.update { it + count }
  }

  override fun acknowledge() {
    state.value = 0
  }
}
