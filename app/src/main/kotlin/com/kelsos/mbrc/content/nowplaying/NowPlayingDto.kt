package com.kelsos.mbrc.content.nowplaying

import com.fasterxml.jackson.annotation.JsonProperty

data class NowPlayingDto(
  @JsonProperty("title")
  var title: String = "",
  @JsonProperty("artist")
  var artist: String = "",
  @JsonProperty("path")
  var path: String = "",
  @JsonProperty("position")
  var position: Int = 0
)
