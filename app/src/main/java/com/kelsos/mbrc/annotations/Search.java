package com.kelsos.mbrc.annotations;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Search {
  public static final int SECTION_GENRE = 0;
  public static final int SECTION_ARTIST = 1;
  public static final int SECTION_ALBUM = 2;
  public static final int SECTION_TRACK = 3;

  @IntDef({
      SECTION_GENRE,
      SECTION_ARTIST,
      SECTION_ALBUM,
      SECTION_TRACK
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface Section {}
}
