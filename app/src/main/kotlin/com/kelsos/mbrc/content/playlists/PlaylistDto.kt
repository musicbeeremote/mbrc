package com.kelsos.mbrc.content.playlists

import com.fasterxml.jackson.annotation.JsonProperty

data class PlaylistDto(
  @JsonProperty
  var name: String = "",
  @JsonProperty
  var url: String = ""
)
