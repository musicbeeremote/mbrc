package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
    Playstate.PAUSED,
    Playstate.PLAYING,
    Playstate.STOPPED
})
public @interface Playstate {
  String PLAYING = "playing";
  String PAUSED = "paused";
  String STOPPED = "stopped";
}
