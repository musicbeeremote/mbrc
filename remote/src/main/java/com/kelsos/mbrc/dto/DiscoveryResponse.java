package com.kelsos.mbrc.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL) @JsonPropertyOrder({
    "context",
    "address",
    "name",
    "port",
    "http"
}) public class DiscoveryResponse {

  @JsonProperty("context") private String context;
  @JsonProperty("address") private String address;
  @JsonProperty("name") private String name;
  @JsonProperty("port") private int port;
  @JsonProperty("http") private int http;
  @JsonIgnore private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  /**
   * @return The context
   */
  @JsonProperty("context") public String getContext() {
    return context;
  }

  /**
   * @param context The context
   */
  @JsonProperty("context") public void setContext(String context) {
    this.context = context;
  }

  /**
   * @return The address
   */
  @JsonProperty("address") public String getAddress() {
    return address;
  }

  /**
   * @param address The address
   */
  @JsonProperty("address") public void setAddress(String address) {
    this.address = address;
  }

  /**
   * @return The name
   */
  @JsonProperty("name") public String getName() {
    return name;
  }

  /**
   * @param name The name
   */
  @JsonProperty("name") public void setName(String name) {
    this.name = name;
  }

  /**
   * @return The port
   */
  @JsonProperty("port") public int getPort() {
    return port;
  }

  /**
   * @param port The port
   */
  @JsonProperty("port") public void setPort(int port) {
    this.port = port;
  }

  /**
   * @return The http
   */
  @JsonProperty("http") public int getHttp() {
    return http;
  }

  /**
   * @param http The http
   */
  @JsonProperty("http") public void setHttp(int http) {
    this.http = http;
  }

  @Override public String toString() {
    return "DiscoveryResponse{" +
        "address='" + address + '\'' +
        ", name='" + name + '\'' +
        ", port=" + port +
        ", http=" + http +
        '}';
  }

  @JsonAnyGetter public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
