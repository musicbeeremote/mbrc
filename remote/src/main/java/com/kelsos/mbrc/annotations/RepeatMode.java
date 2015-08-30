package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({
    RepeatMode.ALL,
    RepeatMode.NONE,
    RepeatMode.CHANGE
})
@Retention(RetentionPolicy.SOURCE)
public @interface RepeatMode {
  String ALL = "all";
  String NONE = "none";
  String CHANGE = "";
}
