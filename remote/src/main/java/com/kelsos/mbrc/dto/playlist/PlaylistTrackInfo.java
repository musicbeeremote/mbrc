package com.kelsos.mbrc.dto.playlist;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "path",
    "artist",
    "title",
    "id",
    "date_added",
    "date_updated",
    "date_deleted"
}) public class PlaylistTrackInfo {

  @JsonProperty("path") private String path;
  @JsonProperty("artist") private String artist;
  @JsonProperty("title") private String title;
  @JsonProperty("id") private Integer id;
  @JsonProperty("date_added") private String dateAdded;
  @JsonProperty("date_updated") private String dateUpdated;
  @JsonProperty("date_deleted") private String dateDeleted;

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
   * @return The id
   */
  @JsonProperty("id") public Integer getId() {
    return id;
  }

  /**
   * @param id The id
   */
  @JsonProperty("id") public void setId(Integer id) {
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

  /**
   * @return The dateUpdated
   */
  @JsonProperty("date_updated") public String getDateUpdated() {
    return dateUpdated;
  }

  /**
   * @param dateUpdated The date_updated
   */
  @JsonProperty("date_updated") public void setDateUpdated(String dateUpdated) {
    this.dateUpdated = dateUpdated;
  }

  /**
   * @return The dateDeleted
   */
  @JsonProperty("date_deleted") public String getDateDeleted() {
    return dateDeleted;
  }

  /**
   * @param dateDeleted The date_deleted
   */
  @JsonProperty("date_deleted") public void setDateDeleted(String dateDeleted) {
    this.dateDeleted = dateDeleted;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(path)
        .append(artist)
        .append(title)
        .append(id)
        .append(dateAdded)
        .append(dateUpdated)
        .append(dateDeleted)
        .toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof PlaylistTrackInfo)) {
      return false;
    }
    PlaylistTrackInfo rhs = ((PlaylistTrackInfo) other);
    return new EqualsBuilder().append(path, rhs.path)
        .append(artist, rhs.artist)
        .append(title, rhs.title)
        .append(id, rhs.id)
        .append(dateAdded, rhs.dateAdded)
        .append(dateUpdated, rhs.dateUpdated)
        .append(dateDeleted, rhs.dateDeleted)
        .isEquals();
  }
}
