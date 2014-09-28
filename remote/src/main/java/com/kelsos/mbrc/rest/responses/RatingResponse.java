package com.kelsos.mbrc.rest.responses;

public class RatingResponse {
    private float rating;

    public RatingResponse(float rating) {
        this.rating = rating;
    }

    public RatingResponse() {}

    public float getRating() {
        return rating;
    }
}
