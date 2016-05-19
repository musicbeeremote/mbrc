package com.kelsos.mbrc.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "url"
}) public class Playlist {

  @JsonProperty private String name;
  @JsonProperty private String url;

  @JsonProperty public String getName() {
    return name;
  }

  @JsonProperty public void setName(String name) {
    this.name = name;
  }

  @JsonProperty public String getUrl() {
    return url;
  }

  @JsonProperty public void setUrl(String url) {
    this.url = url;
  }
}
