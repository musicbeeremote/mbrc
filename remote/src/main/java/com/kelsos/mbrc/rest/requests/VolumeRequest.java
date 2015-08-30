package com.kelsos.mbrc.rest.requests;

public class VolumeRequest {
  final private int value;

  public VolumeRequest(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
