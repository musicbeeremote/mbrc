package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.enums.LfmStatus;

public class LfmRatingChanged {
  private LfmStatus status;

  public LfmRatingChanged(LfmStatus status) {
    this.status = status;
  }

  public LfmStatus getStatus() {
    return status;
  }
}
