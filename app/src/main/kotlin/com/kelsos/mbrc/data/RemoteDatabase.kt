package com.kelsos.mbrc.data

import com.raizlabs.android.dbflow.annotation.Database

@Database(name = RemoteDatabase.NAME, version = RemoteDatabase.VERSION)
object RemoteDatabase {
  const val NAME = "data"
  const val VERSION = 1
}
