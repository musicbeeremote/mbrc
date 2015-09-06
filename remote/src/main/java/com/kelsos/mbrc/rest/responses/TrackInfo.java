package com.kelsos.mbrc.rest.responses;

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
    "album",
    "year",
    "path"
})
public class TrackInfo {

  @JsonProperty("artist")
  private String artist;
  @JsonProperty("title")
  private String title;
  @JsonProperty("album")
  private String album;
  @JsonProperty("year")
  private String year;
  @JsonProperty("path")
  private String path;

  /**
   *
   * @return
   *     The artist
   */
  @JsonProperty("artist")
  public String getArtist() {
    return artist;
  }

  /**
   *
   * @param artist
   *     The artist
   */
  @JsonProperty("artist")
  public void setArtist(String artist) {
    this.artist = artist;
  }

  /**
   *
   * @return
   *     The title
   */
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  /**
   *
   * @param title
   *     The title
   */
  @JsonProperty("title")
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   *
   * @return
   *     The album
   */
  @JsonProperty("album")
  public String getAlbum() {
    return album;
  }

  /**
   *
   * @param album
   *     The album
   */
  @JsonProperty("album")
  public void setAlbum(String album) {
    this.album = album;
  }

  /**
   *
   * @return
   *     The year
   */
  @JsonProperty("year")
  public String getYear() {
    return year;
  }

  /**
   *
   * @param year
   *     The year
   */
  @JsonProperty("year")
  public void setYear(String year) {
    this.year = year;
  }

  /**
   *
   * @return
   *     The path
   */
  @JsonProperty("path")
  public String getPath() {
    return path;
  }

  /**
   *
   * @param path
   *     The path
   */
  @JsonProperty("path")
  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(artist).append(title).append(album).append(year).append(path).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof TrackInfo)) {
      return false;
    }
    TrackInfo rhs = ((TrackInfo) other);
    return new EqualsBuilder().append(artist, rhs.artist).append(title, rhs.title).append(album, rhs.album).append(year, rhs.year).append(path, rhs.path).isEquals();
  }

}
