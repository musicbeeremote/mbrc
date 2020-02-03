package com.kelsos.mbrc.features.queue

data class QueueData(
  @Queue.Action val action: String,
  val paths: List<String> = emptyList(),
  val playPath: String? = null
)
