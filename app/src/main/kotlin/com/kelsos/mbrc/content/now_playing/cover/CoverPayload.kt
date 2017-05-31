package com.kelsos.mbrc.content.now_playing.cover

import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.content.now_playing.cover.CoverPayload.Companion.NOT_FOUND

data class CoverPayload(@JsonProperty("status") val status: Int = NOT_FOUND, @JsonProperty("cover") val cover: String = "") {
  companion object {
    const val READY = 1
    const val SUCCESS = 200
    const val NOT_FOUND = 404
  }
}
