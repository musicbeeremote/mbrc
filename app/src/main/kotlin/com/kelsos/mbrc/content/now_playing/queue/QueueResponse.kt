package com.kelsos.mbrc.content.now_playing.queue

import com.fasterxml.jackson.annotation.JsonProperty

data class QueueResponse(@JsonProperty("code") val code: Int)
