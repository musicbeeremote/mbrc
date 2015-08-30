package com.kelsos.mbrc.rest.requests;

public class RatingRequest {
  public final float rating;

  public RatingRequest(float rating) {
    this.rating = rating;
  }

  public float getRating() {
    return this.rating;
  }
}
