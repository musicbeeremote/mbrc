package com.kelsos.mbrc.features.library.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackDto(
  @Json(name = "artist")
  var artist: String = "",
  @Json(name = "title")
  var title: String = "",
  @Json(name = "src")
  var src: String = "",
  @Json(name = "trackno")
  var trackno: Int = 0,
  @Json(name = "disc")
  var disc: Int = 0,
  @Json(name = "album_artist")
  var albumArtist: String = "",
  @Json(name = "album")
  var album: String = "",
  @Json(name = "genre")
  var genre: String = "",
  @Json(name = "year")
  var year: String = "",
)
