package com.kelsos.mbrc.annotations

import android.support.annotation.StringDef

object MetaDataType {
    const val ARTIST = "artist"
    const val GENRE = "genre"
    const val TRACK = "track"
    const val ALBUM = "album"

    @StringDef(ALBUM, ARTIST, TRACK, GENRE)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Type
}
