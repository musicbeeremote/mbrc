package com.kelsos.mbrc.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.annotations.Queue.QueueType

data class QueuePayload(@JsonProperty("queue") @QueueType val type: String,
                        @JsonProperty("data") val data: List<String>)
