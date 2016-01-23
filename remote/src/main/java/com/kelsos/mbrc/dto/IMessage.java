package com.kelsos.mbrc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by kelsos on 1/23/16.
 */
public interface IMessage {
  @JsonProperty("message") String getMessage();

  @JsonProperty("message") void setMessage(String message);
}
