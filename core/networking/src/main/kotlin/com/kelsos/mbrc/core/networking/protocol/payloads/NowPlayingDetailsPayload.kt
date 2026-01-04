package com.kelsos.mbrc.core.networking.protocol.payloads

import com.kelsos.mbrc.core.common.state.TrackDetails
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Payload for the nowplayingdetails protocol message.
 * Contains extended track metadata and file properties.
 */
@JsonClass(generateAdapter = true)
data class NowPlayingDetailsPayload(
  // Tag metadata
  @Json(name = "albumArtist")
  val albumArtist: String = "",
  @Json(name = "genre")
  val genre: String = "",
  @Json(name = "trackNo")
  val trackNo: String = "",
  @Json(name = "trackCount")
  val trackCount: String = "",
  @Json(name = "discNo")
  val discNo: String = "",
  @Json(name = "discCount")
  val discCount: String = "",
  @Json(name = "grouping")
  val grouping: String = "",
  @Json(name = "publisher")
  val publisher: String = "",
  @Json(name = "ratingAlbum")
  val ratingAlbum: String = "",
  @Json(name = "composer")
  val composer: String = "",
  @Json(name = "comment")
  val comment: String = "",
  @Json(name = "encoder")
  val encoder: String = "",

  // File properties
  @Json(name = "kind")
  val kind: String = "",
  @Json(name = "format")
  val format: String = "",
  @Json(name = "size")
  val size: String = "",
  @Json(name = "channels")
  val channels: String = "",
  @Json(name = "sampleRate")
  val sampleRate: String = "",
  @Json(name = "bitrate")
  val bitrate: String = "",
  @Json(name = "dateModified")
  val dateModified: String = "",
  @Json(name = "dateAdded")
  val dateAdded: String = "",
  @Json(name = "lastPlayed")
  val lastPlayed: String = "",
  @Json(name = "playCount")
  val playCount: String = "",
  @Json(name = "skipCount")
  val skipCount: String = "",
  @Json(name = "duration")
  val duration: String = ""
) {
  /**
   * Converts this payload to a [TrackDetails] domain model.
   */
  fun toTrackDetails(): TrackDetails = TrackDetails(
    albumArtist = albumArtist,
    genre = genre,
    trackNo = trackNo,
    trackCount = trackCount,
    discNo = discNo,
    discCount = discCount,
    grouping = grouping,
    publisher = publisher,
    ratingAlbum = ratingAlbum,
    composer = composer,
    comment = comment,
    encoder = encoder,
    kind = kind,
    format = format,
    size = size,
    channels = channels,
    sampleRate = sampleRate,
    bitrate = bitrate,
    dateModified = dateModified,
    dateAdded = dateAdded,
    lastPlayed = lastPlayed,
    playCount = playCount,
    skipCount = skipCount,
    duration = duration
  )
}
