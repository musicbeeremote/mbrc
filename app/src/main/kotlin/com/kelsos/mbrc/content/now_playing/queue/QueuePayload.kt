package com.kelsos.mbrc.content.now_playing.queue

import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.annotations.Queue.QueueType

data class QueuePayload(@JsonProperty("queue") @QueueType val type: String,
                        @JsonProperty("data") val data: List<String>,
                        @JsonProperty("play") val play: String? = null)
