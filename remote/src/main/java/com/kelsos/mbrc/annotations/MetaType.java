package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({
    MetaType.ALBUM,
    MetaType.ARTIST,
    MetaType.TRACK,
    MetaType.GENRE
})
@Retention(RetentionPolicy.SOURCE)
public @interface MetaType {
  String ARTIST = "artist";
  String GENRE = "genre";
  String TRACK = "track";
  String ALBUM = "album";
}
