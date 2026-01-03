package com.kelsos.mbrc.core.common.settings

sealed class TrackAction(val value: String) {
  data object QueueNext : TrackAction(NEXT)

  data object QueueLast : TrackAction(LAST)

  data object PlayNow : TrackAction(NOW)

  data object PlayNowQueueAll : TrackAction(ADD_ALL)

  companion object {
    const val NEXT = "next"
    const val LAST = "last"
    const val NOW = "now"
    const val ADD_ALL = "add-all"

    fun fromString(action: String): TrackAction = when (action) {
      NEXT -> QueueNext
      LAST -> QueueLast
      NOW -> PlayNow
      ADD_ALL -> PlayNowQueueAll
      else -> QueueLast
    }
  }
}
