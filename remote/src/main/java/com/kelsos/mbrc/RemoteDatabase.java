package com.kelsos.mbrc;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = RemoteDatabase.NAME, version = 1)
public class RemoteDatabase {
  public static final String NAME = "library";
  public static final int VERSION = 1;
}
