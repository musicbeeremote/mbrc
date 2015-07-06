package com.kelsos.mbrc.data;

import com.fasterxml.jackson.databind.JsonNode;

public class GenreEntry {
  private int count;
  private String name;

  public GenreEntry(JsonNode node) {
    this.name = node.path("genre").textValue();
    this.count = node.path("count").intValue();
  }

  @SuppressWarnings("unused") public int getCount() {
    return count;
  }

  public String getName() {
    return name;
  }
}
