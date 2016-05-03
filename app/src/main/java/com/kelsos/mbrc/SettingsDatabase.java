package com.kelsos.mbrc;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = SettingsDatabase.NAME, version = SettingsDatabase.VERSION)
public class SettingsDatabase {
  static final String NAME = "settings";
  static final int VERSION = 1;
}
