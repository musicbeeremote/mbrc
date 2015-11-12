package com.kelsos.mbrc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "artist",
    "title",
    "position",
    "path",
    "id",
    "date_added"
})
public class NowPlayingTrack {

  @JsonProperty("artist") private String artist;
  @JsonProperty("title") private String title;
  @JsonProperty("position") private int position;
  @JsonProperty("path") private String path;
  @JsonProperty("id") private int id;
  @JsonProperty("date_added") private String dateAdded;
  @JsonProperty("date_updated") private String dateUpdated;
  @JsonProperty("date_deleted") private String dateDeleted;

  /**
   * @return The artist
   */
  @JsonProperty("artist") public String getArtist() {
    return artist;
  }

  /**
   * @param artist The artist
   */
  @JsonProperty("artist") public void setArtist(String artist) {
    this.artist = artist;
  }

  /**
   * @return The title
   */
  @JsonProperty("title") public String getTitle() {
    return title;
  }

  /**
   * @param title The title
   */
  @JsonProperty("title") public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return The position
   */
  @JsonProperty("position") public int getPosition() {
    return position;
  }

  /**
   * @param position The position
   */
  @JsonProperty("position") public void setPosition(int position) {
    this.position = position;
  }

  /**
   * @return The path
   */
  @JsonProperty("path") public String getPath() {
    return path;
  }

  /**
   * @param path The path
   */
  @JsonProperty("path") public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return The id
   */
  @JsonProperty("id") public int getId() {
    return id;
  }

  /**
   * @param id The id
   */
  @JsonProperty("id") public void setId(int id) {
    this.id = id;
  }

  /**
   * @return The dateAdded
   */
  @JsonProperty("date_added") public String getDateAdded() {
    return dateAdded;
  }

  /**
   * @param dateAdded The date_added
   */
  @JsonProperty("date_added") public void setDateAdded(String dateAdded) {
    this.dateAdded = dateAdded;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(artist)
        .append(title)
        .append(position)
        .append(path)
        .append(id)
        .append(dateAdded)
        .toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof NowPlayingTrack)) {
      return false;
    }
    NowPlayingTrack rhs = ((NowPlayingTrack) other);
    return new EqualsBuilder().append(artist, rhs.artist)
        .append(title, rhs.title)
        .append(position, rhs.position)
        .append(path, rhs.path)
        .append(id, rhs.id)
        .append(dateAdded, rhs.dateAdded)
        .isEquals();
  }

  @JsonProperty("date_updated") public String getDateUpdated() {
    return dateUpdated;
  }

  @JsonProperty("date_updated") public void setDateUpdated(String dateUpdated) {
    this.dateUpdated = dateUpdated;
  }

  @JsonProperty("date_deleted") public String getDateDeleted() {
    return dateDeleted;
  }

  @JsonProperty("date_deleted") public void setDateDeleted(String dateDeleted) {
    this.dateDeleted = dateDeleted;
  }
}
