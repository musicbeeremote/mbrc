package com.kelsos.mbrc.data.db

import com.raizlabs.android.dbflow.annotation.Database

@Database(version = CacheDatabase.VERSION, name = CacheDatabase.NAME)
object CacheDatabase {
    const val VERSION = 1
    const val NAME = "cache"
}
