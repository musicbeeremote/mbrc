package com.kelsos.mbrc.data.db

import com.raizlabs.android.dbflow.annotation.Database

@Database(version = RemoteDatabase.VERSION, name = RemoteDatabase.NAME)
object RemoteDatabase {
  const val VERSION = 1
  const val NAME = "cache"
}
