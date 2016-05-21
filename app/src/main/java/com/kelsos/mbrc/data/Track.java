package com.kelsos.mbrc.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "artist",
    "title",
    "src",
    "trackno",
    "disc"
}) public class Track {
  @JsonProperty("artist") private String artist;
  @JsonProperty("title") private String title;
  @JsonProperty("src") private String src;
  @JsonProperty("trackno") private int trackno;
  @JsonProperty("disc") private int disc;

  public Track(JsonNode jNode) {
    this.artist = jNode.path("artist").textValue();
    this.title = jNode.path("title").textValue();
    this.src = jNode.path("src").textValue();
  }

  public Track() {

  }

  @JsonProperty("artist") public String getArtist() {
    return artist;
  }

  @JsonProperty("artist") public void setArtist(String artist) {
    this.artist = artist;
  }

  @JsonProperty("title") public String getTitle() {
    return title;
  }

  @JsonProperty("title") public void setTitle(String title) {
    this.title = title;
  }

  @JsonProperty("src") public String getSrc() {
    return src;
  }

  @JsonProperty("src") public void setSrc(String src) {
    this.src = src;
  }

  @JsonProperty("trackno") public int getTrackno() {
    return trackno;
  }

  @JsonProperty("trackno") public void setTrackno(int trackno) {
    this.trackno = trackno;
  }

  @JsonProperty("disc") public int getDisc() {
    return disc;
  }

  @JsonProperty("disc") public void setDisc(int disc) {
    this.disc = disc;
  }
}
