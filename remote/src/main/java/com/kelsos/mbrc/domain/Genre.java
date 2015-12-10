package com.kelsos.mbrc.domain;

public class Genre {
  private long id;
  private String name;

  public Genre(long id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public long getId() {
    return id;
  }
}
