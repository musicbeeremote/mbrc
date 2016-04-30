package com.kelsos.mbrc.events.ui

import com.kelsos.mbrc.annotations.Repeat

class RepeatChange {
  @Repeat.Mode val mode: String

  constructor(@Repeat.Mode mode: String) {
    this.mode = mode
  }

}
