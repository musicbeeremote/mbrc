package com.kelsos.mbrc

import com.raizlabs.android.dbflow.annotation.Database

@Database(name = SettingsDatabase.NAME, version = SettingsDatabase.VERSION)
object SettingsDatabase {
  const val NAME = "library"
  const val VERSION = 1
}
