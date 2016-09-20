package com.kelsos.mbrc.data.db

import com.raizlabs.android.dbflow.annotation.Database

@Database(name = SettingsDatabase.NAME, version = SettingsDatabase.VERSION)
object SettingsDatabase {
    const val VERSION = 1
    const val NAME = "hosts"
}
