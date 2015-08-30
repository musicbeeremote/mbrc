package com.kelsos.mbrc.rest.requests;

public class PlayPathRequest {
  private final String path;

  public PlayPathRequest(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
