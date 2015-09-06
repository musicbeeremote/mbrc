package com.kelsos.mbrc.rest.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "rating"
})
public class RatingResponse {

  @JsonProperty("rating")
  private double rating;

  /**
   *
   * @return
   *     The rating
   */
  @JsonProperty("rating")
  public double getRating() {
    return rating;
  }

  /**
   *
   * @param rating
   *     The rating
   */
  @JsonProperty("rating")
  public void setRating(double rating) {
    this.rating = rating;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(rating).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof RatingResponse)) {
      return false;
    }
    RatingResponse rhs = ((RatingResponse) other);
    return new EqualsBuilder().append(rating, rhs.rating).isEquals();
  }

}
