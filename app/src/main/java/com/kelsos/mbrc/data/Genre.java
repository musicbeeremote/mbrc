package com.kelsos.mbrc.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "genre",
    "count"
})
public class Genre {

  @JsonProperty("genre")
  private String genre;
  @JsonProperty("count")
  private int count;

  @JsonProperty("genre")
  public String getGenre() {
    return genre;
  }

  @JsonProperty("genre")
  public void setGenre(String genre) {
    this.genre = genre;
  }

  @JsonProperty("count")
  public int getCount() {
    return count;
  }

  @JsonProperty("count")
  public void setCount(int count) {
    this.count = count;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(genre).append(count).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof Genre)) {
      return false;
    }
    Genre rhs = ((Genre) other);
    return new EqualsBuilder().append(genre, rhs.genre).append(count, rhs.count).isEquals();
  }

}

