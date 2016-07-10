package com.kelsos.mbrc.data.db;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = SettingsDatabase.NAME, version = SettingsDatabase.VERSION)
public class SettingsDatabase {
  static final int VERSION = 1;
  static final String NAME = "hosts";
}
