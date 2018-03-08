package com.kelsos.mbrc.content.nowplaying.queue

import com.fasterxml.jackson.annotation.JsonProperty

data class QueueResponse(@JsonProperty("code") val code: Int)