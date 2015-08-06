package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.GenreEntry;
import java.util.ArrayList;

public class GenreSearchResults {
  private ArrayList<GenreEntry> list;
  private boolean stored;

  public GenreSearchResults(ArrayList<GenreEntry> list, boolean stored) {
    this.list = list;
    this.stored = stored;
  }

  public ArrayList<GenreEntry> getList() {
    return this.list;
  }

  public boolean isStored() {
    return stored;
  }
}
