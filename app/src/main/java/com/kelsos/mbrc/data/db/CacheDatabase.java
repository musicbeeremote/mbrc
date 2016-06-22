package com.kelsos.mbrc.data.db;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(version = CacheDatabase.VERSION, name = CacheDatabase.NAME)
public class CacheDatabase {
  static final int VERSION = 1;
  static final String NAME = "cache";
}
