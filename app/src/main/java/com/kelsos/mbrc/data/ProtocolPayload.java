package com.kelsos.mbrc.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "no_broadcast",
    "protocol_version"
})
public class ProtocolPayload {

  @JsonProperty("no_broadcast")
  private Boolean noBroadcast;
  @JsonProperty("protocol_version")
  private Float protocolVersion;

  @JsonProperty("no_broadcast") public Boolean getNoBroadcast() {
    return noBroadcast;
  }

  @JsonProperty("no_broadcast") public void setNoBroadcast(Boolean noBroadcast) {
    this.noBroadcast = noBroadcast;
  }

  @JsonProperty("protocol_version") public Float getProtocolVersion() {
    return protocolVersion;
  }

  @JsonProperty("protocol_version") public void setProtocolVersion(Float protocolVersion) {
    this.protocolVersion = protocolVersion;
  }
}
