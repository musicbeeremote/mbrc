package com.kelsos.mbrc.core.platform.state

import android.os.Parcel
import android.os.Parcelable
import com.kelsos.mbrc.core.common.state.TrackInfo

data class PlayingTrack(
  override val artist: String = "",
  override val title: String = "",
  override val album: String = "",
  override val year: String = "",
  override val path: String = "",
  override val coverUrl: String = "",
  override val duration: Long = 0
) : TrackInfo,
  Parcelable {
  companion object {
    @JvmField
    val CREATOR: Parcelable.Creator<PlayingTrack> =
      object : Parcelable.Creator<PlayingTrack> {
        override fun createFromParcel(source: Parcel): PlayingTrack = PlayingTrack(source)

        override fun newArray(size: Int): Array<PlayingTrack?> = arrayOfNulls(size)
      }
  }

  constructor(source: Parcel) : this(
    source.readString().orEmpty(),
    source.readString().orEmpty(),
    source.readString().orEmpty(),
    source.readString().orEmpty(),
    source.readString().orEmpty(),
    source.readString().orEmpty(),
    source.readLong()
  )

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeString(artist)
    dest.writeString(title)
    dest.writeString(album)
    dest.writeString(year)
    dest.writeString(path)
    dest.writeString(coverUrl)
    dest.writeLong(duration)
  }
}

fun PlayingTrack?.orEmpty() = this ?: PlayingTrack()

/**
 * Converts any TrackInfo implementation to a PlayingTrack.
 * If already a PlayingTrack, returns as-is.
 */
fun TrackInfo.toPlayingTrack(): PlayingTrack = when (this) {
  is PlayingTrack -> this

  else -> PlayingTrack(
    artist = artist,
    title = title,
    album = album,
    year = year,
    path = path,
    coverUrl = coverUrl,
    duration = duration
  )
}
