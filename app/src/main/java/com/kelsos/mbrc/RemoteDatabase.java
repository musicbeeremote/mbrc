package com.kelsos.mbrc;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = RemoteDatabase.NAME, version = RemoteDatabase.VERSION)
public class RemoteDatabase {
  static final String NAME = "data";
  static final int VERSION = 1;
}
