package com.kelsos.mbrc.content.lyrics

import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.content.lyrics.LyricsPayload.Companion.NOT_FOUND

data class LyricsPayload(@JsonProperty("status") val status: Int = NOT_FOUND, @JsonProperty("lyrics") val lyrics: String = "") {

  companion object {
    const val SUCCESS = 200
    const val NOT_FOUND = 404
  }
}

