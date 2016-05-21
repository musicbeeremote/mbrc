package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.Genre;
import java.util.ArrayList;

public class GenreSearchResults {
  private ArrayList<Genre> list;
  private boolean stored;

  public GenreSearchResults(ArrayList<Genre> list, boolean stored) {
    this.list = list;
    this.stored = stored;
  }

  public ArrayList<Genre> getList() {
    return this.list;
  }

  public boolean isStored() {
    return stored;
  }
}
