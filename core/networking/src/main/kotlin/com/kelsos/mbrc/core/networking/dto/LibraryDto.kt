package com.kelsos.mbrc.core.networking.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenreDto(
  @Json(name = "genre")
  val genre: String = "",
  @Json(name = "count")
  val count: Int = 0
)

@JsonClass(generateAdapter = true)
data class ArtistDto(
  @Json(name = "artist")
  val artist: String = "",
  @Json(name = "count")
  val count: Int = 0
)

@JsonClass(generateAdapter = true)
data class AlbumDto(
  @Json(name = "artist")
  val artist: String = "",
  @Json(name = "album")
  val album: String = "",
  @Json(name = "count")
  val count: Int = 0
)

@JsonClass(generateAdapter = true)
data class AlbumCoverDto(
  @Json(name = "artist")
  val artist: String?,
  @Json(name = "album")
  val album: String?,
  @Json(name = "hash")
  val hash: String?
)

@JsonClass(generateAdapter = true)
data class TrackDto(
  @Json(name = "artist")
  val artist: String = "",
  @Json(name = "title")
  val title: String = "",
  @Json(name = "src")
  val src: String = "",
  @Json(name = "trackno")
  val trackno: Int = 0,
  @Json(name = "disc")
  val disc: Int = 0,
  @Json(name = "album_artist")
  val albumArtist: String = "",
  @Json(name = "album")
  val album: String = "",
  @Json(name = "genre")
  val genre: String = "",
  @Json(name = "year")
  val year: String = ""
)

@JsonClass(generateAdapter = true)
data class NowPlayingDto(
  @Json(name = "title")
  val title: String = "",
  @Json(name = "artist")
  val artist: String = "",
  @Json(name = "path")
  val path: String = "",
  @Json(name = "position")
  val position: Int = 0
)

@JsonClass(generateAdapter = true)
data class PlaylistDto(
  @Json(name = "name")
  val name: String = "",
  @Json(name = "url")
  val url: String = ""
)

@JsonClass(generateAdapter = true)
data class RadioStationDto(
  @Json(name = "name")
  val name: String = "",
  @Json(name = "url")
  val url: String = ""
)

@JsonClass(generateAdapter = true)
data class CoverDto(
  @Json(name = "status")
  val status: Int,
  @Json(name = "cover")
  val cover: String?,
  @Json(name = "hash")
  val hash: String?
)
