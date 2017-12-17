package com.kelsos.mbrc.content.library.albums

import com.fasterxml.jackson.annotation.JsonProperty

data class AlbumDto(
  @JsonProperty("artist")
  var artist: String = "",
  @JsonProperty("album")
  var album: String = ""
)
