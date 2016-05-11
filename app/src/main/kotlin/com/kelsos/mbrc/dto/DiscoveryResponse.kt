package com.kelsos.mbrc.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("context", "address", "name", "port", "http")
class DiscoveryResponse {

  @JsonProperty("context") var context: String? = null

  @JsonProperty("address") var address: String? = null

  @JsonProperty("name") var name: String? = null

  @JsonProperty("port") var port: Int = 0

  override fun toString(): String {
    return "DiscoveryResponse{address='$address\', name='$name\', port=$port}"
  }

}
