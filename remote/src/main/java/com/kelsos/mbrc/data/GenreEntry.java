package com.kelsos.mbrc.data;

import org.codehaus.jackson.JsonNode;

public class GenreEntry {
  private int count;
  private String name;

  public GenreEntry(JsonNode node) {
    this.name = node.path("genre").getTextValue();
    this.count = node.path("count").getIntValue();
  }

  @SuppressWarnings("unused") public int getCount() {
    return count;
  }

  public String getName() {
    return name;
  }
}
