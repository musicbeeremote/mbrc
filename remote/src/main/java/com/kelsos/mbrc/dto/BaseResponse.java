package com.kelsos.mbrc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL) @JsonPropertyOrder({
    "code",
    "message",
    "description"
}) public class BaseResponse {

  @JsonProperty("code") private int code;
  @JsonProperty("message") private String message;
  @JsonProperty("description") private String description;

  /**
   * @return The code status of the request
   */
  @JsonProperty("code") public int getCode() {
    return code;
  }

  /**
   * @param code The code status of the request
   */
  @JsonProperty("code") public void setCode(int code) {
    this.code = code;
  }

  @JsonProperty("message") public String getMessage() {
    return message;
  }

  @JsonProperty("message") public void setMessage(String message) {
    this.message = message;
  }

  @JsonProperty("description") public String getDescription() {
    return description;
  }

  @JsonProperty("description") public void setDescription(String description) {
    this.description = description;
  }

  @Override public int hashCode() {
    int result = code;
    result = 31 * result + (message != null ? message.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BaseResponse that = (BaseResponse) o;

    if (code != that.code) return false;
    if (message != null ? !message.equals(that.message) : that.message != null) return false;
    return description != null ? description.equals(that.description) : that.description == null;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
