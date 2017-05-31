package com.kelsos.mbrc.content.now_playing.queue

import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.content.now_playing.queue.Queue.Action

data class QueuePayload(
  @JsonProperty("queue") @Action val type: String,
  @JsonProperty("data") val data: List<String>,
  @JsonProperty("play") val play: String? = null
)
