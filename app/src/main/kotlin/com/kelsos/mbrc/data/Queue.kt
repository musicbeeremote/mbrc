package com.kelsos.mbrc.data

import android.support.annotation.StringDef
import com.fasterxml.jackson.annotation.JsonProperty


class Queue(@QueueType @JsonProperty val type: String, @JsonProperty val query: String) {

  @StringDef(NEXT, LAST, NOW)
  @Retention(AnnotationRetention.SOURCE)
  annotation class QueueType

  companion object {
    const val NEXT = "next"
    const val LAST = "last"
    const val NOW = "now"
  }
}
