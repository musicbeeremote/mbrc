package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
    PlaybackAction.NEXT,
    PlaybackAction.STOP,
    PlaybackAction.PLAY,
    PlaybackAction.PAUSE,
    PlaybackAction.PREVIOUS,
    PlaybackAction.PLAY_PLAUSE
})
public @interface PlaybackAction {
  String STOP = "stop";
  String PLAY = "play";
  String PAUSE = "pause";
  String NEXT = "next";
  String PREVIOUS = "previous";
  String PLAY_PLAUSE = "playpause";
}
