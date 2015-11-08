package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MetaDataType {
  public static final String ARTIST = "artist";
  public static final String GENRE = "genre";
  public static final String TRACK = "track";
  public static final String ALBUM = "album";
  private MetaDataType() {
    //no instance
  }

  @StringDef({
                 ALBUM,
                 ARTIST,
                 TRACK,
                 GENRE
             })
  @Retention(RetentionPolicy.SOURCE)
  public @interface Type {
  }
}
