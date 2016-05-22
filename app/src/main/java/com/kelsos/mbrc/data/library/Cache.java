package com.kelsos.mbrc.data.library;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(version = Cache.VERSION, name = Cache.NAME)
public class Cache {
  public static final int VERSION = 1;
  public static final String NAME = "cache";
}
