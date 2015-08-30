package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({
    ShuffleState.OFF,
    ShuffleState.AUTODJ,
    ShuffleState.ON,
    ShuffleState.TOGGLE
})

@Retention(RetentionPolicy.SOURCE)
public @interface ShuffleState {
  String OFF = "off";
  String AUTODJ = "autodj";
  String ON = "on";
  String TOGGLE = "";
}
