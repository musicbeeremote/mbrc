package com.kelsos.mbrc.rest.responses;

public class ValueResponse {
  private int value;

  public ValueResponse(int value) {
    this.value = value;
  }

  public ValueResponse() { }

  public int getValue() {
    return value;
  }
}
