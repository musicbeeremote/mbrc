package com.kelsos.mbrc.events.general;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ClearCachedSearchResults {

  private final int type;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ ResultType.GENRE, ResultType.ARTIST, ResultType.ALBUM, ResultType.TRACK })
  public @interface ResultType {
    int GENRE = 0;
    int ARTIST = 1;
    int ALBUM = 2;
    int TRACK = 3;
  }

  public ClearCachedSearchResults(@ResultType int type) {
    this.type = type;
  }

  @ResultType public int getType() {
    return this.type;
  }
}
