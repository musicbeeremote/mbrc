package com.kelsos.mbrc.rest.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "repeat",
    "mute",
    "shuffle",
    "scrobble",
    "state",
    "volume"
}) public class PlayerStatusResponse {

  @JsonProperty("repeat") private String repeat;
  @JsonProperty("mute") private Boolean mute;
  @JsonProperty("shuffle") private String shuffle;
  @JsonProperty("scrobble") private Boolean scrobble;
  @JsonProperty("state") private String state;
  @JsonProperty("volume") private Integer volume;

  /**
   * @return The repeat
   */
  @JsonProperty("repeat") public String getRepeat() {
    return repeat;
  }

  /**
   * @param repeat The repeat
   */
  @JsonProperty("repeat") public void setRepeat(String repeat) {
    this.repeat = repeat;
  }

  /**
   * @return The mute
   */
  @JsonProperty("mute") public Boolean getMute() {
    return mute;
  }

  /**
   * @param mute The mute
   */
  @JsonProperty("mute") public void setMute(Boolean mute) {
    this.mute = mute;
  }

  /**
   * @return The shuffle
   */
  @JsonProperty("shuffle") public String getShuffle() {
    return shuffle;
  }

  /**
   * @param shuffle The shuffle
   */
  @JsonProperty("shuffle") public void setShuffle(String shuffle) {
    this.shuffle = shuffle;
  }

  /**
   * @return The scrobble
   */
  @JsonProperty("scrobble") public Boolean getScrobble() {
    return scrobble;
  }

  /**
   * @param scrobble The scrobble
   */
  @JsonProperty("scrobble") public void setScrobble(Boolean scrobble) {
    this.scrobble = scrobble;
  }

  /**
   * @return The state
   */
  @JsonProperty("state") public String getState() {
    return state;
  }

  /**
   * @param state The state
   */
  @JsonProperty("state") public void setState(String state) {
    this.state = state;
  }

  /**
   * @return The volume
   */
  @JsonProperty("volume") public Integer getVolume() {
    return volume;
  }

  /**
   * @param volume The volume
   */
  @JsonProperty("volume") public void setVolume(Integer volume) {
    this.volume = volume;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(repeat)
        .append(mute)
        .append(shuffle)
        .append(scrobble)
        .append(state)
        .append(volume)
        .toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof PlayerStatusResponse)) {
      return false;
    }
    PlayerStatusResponse rhs = ((PlayerStatusResponse) other);
    return new EqualsBuilder().append(repeat, rhs.repeat)
        .append(mute, rhs.mute)
        .append(shuffle, rhs.shuffle)
        .append(scrobble, rhs.scrobble)
        .append(state, rhs.state)
        .append(volume, rhs.volume)
        .isEquals();
  }
}
