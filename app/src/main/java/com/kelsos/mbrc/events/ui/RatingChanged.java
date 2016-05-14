package com.kelsos.mbrc.events.ui;

public class RatingChanged {
  private float rating;

  public RatingChanged(float rating) {
    this.rating = rating;
  }

  public float getRating() {
    return this.rating;
  }
}
