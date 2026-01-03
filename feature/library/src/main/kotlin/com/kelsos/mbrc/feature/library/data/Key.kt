package com.kelsos.mbrc.feature.library.data

import com.kelsos.mbrc.core.data.library.album.AlbumCover
import okio.ByteString.Companion.encodeUtf8

fun AlbumCover.key(): String = "${artist}_$album".encodeUtf8().sha1().hex().uppercase()
