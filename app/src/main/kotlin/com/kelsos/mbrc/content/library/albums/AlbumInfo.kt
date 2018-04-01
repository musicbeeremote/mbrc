package com.kelsos.mbrc.content.library.albums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlbumInfo(
  val album: String,
  val artist: String,
  val cover: String?
) : Parcelable
