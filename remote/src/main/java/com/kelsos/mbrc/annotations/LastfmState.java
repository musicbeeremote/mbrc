package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
    LastfmState.BAN,
    LastfmState.LOVE,
    LastfmState.NORMAL
})
public @interface LastfmState {
  String LOVE = "Love";
  String BAN = "Ban";
  String NORMAL = "Normal";
}
