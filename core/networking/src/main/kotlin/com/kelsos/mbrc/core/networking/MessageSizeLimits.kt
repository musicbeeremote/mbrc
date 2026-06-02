package com.kelsos.mbrc.core.networking

private const val HEAP_FRACTION = 0.10

// Floor: comfortably above the worst-case single page response (LIMIT=800 items,
// ~3-4 MB even with long, multibyte metadata) so legitimate traffic never trips it.
private const val MIN_MESSAGE_BYTES = 8L * 1024 * 1024

// Ceiling: a single message larger than this is abnormal on any device and would
// threaten the heap, so we refuse it regardless of how much memory is available.
private const val MAX_MESSAGE_BYTES = 64L * 1024 * 1024

/**
 * Heap-relative ceiling for a single inbound protocol message (one line terminated by `\r\n`).
 *
 * The cap auto-scales to the device: a fraction of the runtime's max heap, clamped to a sane
 * range. This keeps the bound generous where there is room (large/largeHeap devices) and
 * protective where there is not (constrained devices), guaranteeing that no single message can
 * grow large enough to exhaust the heap. The terminating value is only a safety ceiling, not a
 * tight fit around expected traffic.
 *
 * [maxHeapBytes] is injectable so tests can exercise the clamping and the oversized-message path.
 */
fun defaultMaxMessageBytes(maxHeapBytes: Long = Runtime.getRuntime().maxMemory()): Long =
  (maxHeapBytes * HEAP_FRACTION).toLong().coerceIn(MIN_MESSAGE_BYTES, MAX_MESSAGE_BYTES)
