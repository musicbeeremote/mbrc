package com.kelsos.mbrc.features.queue

data class QueueData(
  val paths: List<String> = emptyList(),
  val playPath: String? = null,
)
