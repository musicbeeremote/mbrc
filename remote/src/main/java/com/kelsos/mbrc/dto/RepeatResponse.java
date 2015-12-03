package com.kelsos.mbrc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kelsos.mbrc.annotations.Repeat.Mode;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "value",
    "code"
})
public class RepeatResponse {

  @JsonProperty("value") private String value;
  @JsonProperty("code") private int code;

  @JsonProperty("value") public void setValue(@Mode String value) {
    this.value = value;
  }

  @JsonProperty("code") public void setCode(int code) {
    this.code = code;
  }

  @JsonProperty("value") @Mode public String getValue() {
    return value;
  }

  @JsonProperty("code") public int getCode() {
    return code;
  }
}
