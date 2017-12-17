package com.kelsos.mbrc.content.nowplaying.queue

import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.content.nowplaying.queue.Queue.QueueType

data class QueuePayload(
    @JsonProperty("queue") @QueueType val type: String,
    @JsonProperty("data") val data: List<String>,
    @JsonProperty("play") val play: String? = null
)
