package com.kelsos.mbrc.content.library.tracks

import com.fasterxml.jackson.annotation.JsonProperty

data class TrackDto(
  @JsonProperty("artist")
  var artist: String = "",
  @JsonProperty("title")
  var title: String = "",
  @JsonProperty("src")
  var src: String = "",
  @JsonProperty("trackno")
  var trackno: Int = 0,
  @JsonProperty("disc")
  var disc: Int = 0,
  @JsonProperty("album_artist")
  var albumArtist: String = "",
  @JsonProperty("album")
  var album: String = "",
  @JsonProperty("genre")
  var genre: String = "",
  @JsonProperty("year")
  var year: String = ""
)