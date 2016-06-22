package com.kelsos.mbrc.data.db;

import com.raizlabs.android.dbflow.annotation.Database;
@Database(version = NowPlayingDatabase.VERSION, name = NowPlayingDatabase.NAME)
public class NowPlayingDatabase {
  static final int VERSION = 1;
  static final String NAME = "now_playing";
}
