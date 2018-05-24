package com.kelsos.mbrc.content.nowplaying.queue

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QueueResponse(@Json(name = "code") val code: Int)