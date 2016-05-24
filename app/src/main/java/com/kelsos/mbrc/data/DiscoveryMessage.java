package com.kelsos.mbrc.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscoveryMessage {
  @JsonProperty("name")
  private String name;
  @JsonProperty("address")
  private String address;
  @JsonProperty("port")
  private int port;
  @JsonProperty("context")
  private String context;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  @Override
  public String toString() {
    return "{" +
        "name='" + name + '\'' +
        ", address='" + address + '\'' +
        ", port=" + port +
        ", context='" + context + '\'' +
        '}';
  }
}
