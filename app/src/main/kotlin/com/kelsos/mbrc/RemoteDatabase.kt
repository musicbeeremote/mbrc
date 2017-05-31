package com.kelsos.mbrc

import com.raizlabs.android.dbflow.annotation.Database

@Database(version = RemoteDatabase.VERSION, name = RemoteDatabase.NAME)
object RemoteDatabase {
    const val VERSION = 2
    const val NAME = "cache"
}
