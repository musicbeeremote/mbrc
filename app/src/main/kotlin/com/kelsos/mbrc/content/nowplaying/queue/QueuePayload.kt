package com.kelsos.mbrc.content.nowplaying.queue

import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.Action

data class QueuePayload(
  @JsonProperty("queue") @Action val type: String,
  @JsonProperty("data") val data: List<String>,
  @JsonProperty("play") val play: String? = null
)
