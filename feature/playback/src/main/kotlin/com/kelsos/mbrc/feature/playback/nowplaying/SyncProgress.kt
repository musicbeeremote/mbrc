package com.kelsos.mbrc.feature.playback.nowplaying

/**
 * Progress of an in-flight now-playing refresh. `null` (see [NowPlayingRepository.syncProgress])
 * means no refresh is running.
 *
 * [total] is the server-reported queue size for the current page; it is `0` until the first page
 * arrives, which [isDeterminate] reports as an indeterminate state so the UI can show a spinning
 * bar until a real total is known.
 */
data class SyncProgress(val current: Int, val total: Int) {
  val isDeterminate: Boolean get() = total > 0

  val fraction: Float
    get() = if (total > 0) (current.toFloat() / total).coerceIn(0f, 1f) else 0f
}
