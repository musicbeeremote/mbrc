package com.kelsos.mbrc.core.networking.protocol

fun interface Clock {
  fun now(): Long
}

data class SelfMutationConfig(val windowMs: Long = DEFAULT_WINDOW_MS) {
  companion object {
    const val DEFAULT_WINDOW_MS: Long = 3_000L
  }
}

/**
 * Tracks recent self-initiated mutations to the now-playing list (move, remove)
 * so the broadcast handler can skip the redundant `getRemote()` refresh that
 * would otherwise fire a second `PagingSource` invalidation right after our
 * own local DB write — that cascade is what made the queue view jump back to
 * the loaded-page boundary after a drop.
 *
 * TODO: replace with a delta-sync in `NowPlayingRepository.getRemote()` that
 *  only invalidates rows that actually changed. The wall-clock window here is
 *  a defense-in-depth band-aid: it suppresses *all* refreshes during the window,
 *  not just self-caused ones, which is correct in practice but coarser than it
 *  needs to be.
 */
class SelfMutationTracker(private val clock: Clock, config: SelfMutationConfig) {
  private val windowMs: Long = config.windowMs

  @Volatile
  private var lastMarkedAt: Long = NEVER

  fun mark() {
    lastMarkedAt = clock.now()
  }

  fun wasRecentlyMarked(): Boolean {
    val marked = lastMarkedAt
    if (marked == NEVER) return false
    return clock.now() - marked < windowMs
  }

  private companion object {
    const val NEVER: Long = Long.MIN_VALUE
  }
}
