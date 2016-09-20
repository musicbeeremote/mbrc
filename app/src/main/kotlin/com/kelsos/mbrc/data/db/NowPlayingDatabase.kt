package com.kelsos.mbrc.data.db

import com.raizlabs.android.dbflow.annotation.Database

@Database(version = NowPlayingDatabase.VERSION, name = NowPlayingDatabase.NAME)
object NowPlayingDatabase {
    const val VERSION = 1
    const val NAME = "now_playing_data"
}
