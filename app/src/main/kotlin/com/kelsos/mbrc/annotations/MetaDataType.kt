package com.kelsos.mbrc.annotations

import android.support.annotation.StringDef

object MetaDataType {
    const val ARTIST = "artist"
    const val GENRE = "genre"
    const val TRACK = "track"
    const val ALBUM = "album"
    const val UNDEF = "undef"

    @StringDef(ALBUM, ARTIST, TRACK, GENRE, UNDEF)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Type
}
