package com.kelsos.mbrc.data

import com.raizlabs.android.dbflow.annotation.Database

@Database(name = SettingsDatabase.NAME, version = SettingsDatabase.VERSION)
object SettingsDatabase {
  const val NAME = "settings"
  const val VERSION = 1
}
