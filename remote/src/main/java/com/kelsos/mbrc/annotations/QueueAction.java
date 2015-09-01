package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({
    QueueAction.NEXT,
    QueueAction.LAST,
    QueueAction.NOW
})

@Retention(RetentionPolicy.SOURCE)
public @interface QueueAction {
  String NEXT = "next";
  String LAST = "last";
  String NOW = "now";
}
