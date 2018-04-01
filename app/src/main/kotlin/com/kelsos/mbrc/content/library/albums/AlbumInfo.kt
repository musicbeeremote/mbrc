package com.kelsos.mbrc.content.library.albums

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AlbumInfo(val album: String, val artist: String) : Parcelable