package com.kelsos.mbrc.core.platform.media

import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.kelsos.mbrc.core.platform.state.PlayingTrack

@OptIn(UnstableApi::class)
fun PlayingTrack.toMediaItem(): MediaItem {
  val metadataBuilder =
    MediaMetadata
      .Builder()
      .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
      .setTitle(title)
      .setArtist(artist)
      .setAlbumTitle(album)
      .setArtworkUri(coverUrl.toUri())
      .setReleaseYear(year.toIntOrNull() ?: 0)
      .setDisplayTitle(title)
      .setSubtitle(artist)
      .setDescription(album)

  // Only set duration for valid values (>= 0)
  // For streams with unknown duration (-1), leave it unset
  if (duration >= 0) {
    metadataBuilder.setDurationMs(duration)
  }

  val metadata = metadataBuilder.build()

  return MediaItem
    .Builder()
    .setMediaId(path)
    .setMediaMetadata(metadata)
    .build()
}
