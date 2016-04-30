package com.kelsos.mbrc

import com.raizlabs.android.dbflow.annotation.Database

@Database(name = RemoteDatabase.NAME, version = RemoteDatabase.VERSION)
object RemoteDatabase {
  const val NAME = "library"
  const val VERSION = 1
}
