package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.Artist;
import java.util.ArrayList;

public class ArtistSearchResults {
  private ArrayList<Artist> list;
  private boolean stored;

  public ArtistSearchResults(ArrayList<Artist> list, boolean stored) {
    this.list = list;
    this.stored = stored;
  }

  public ArrayList<Artist> getList() {
    return this.list;
  }

  public boolean isStored() {
    return stored;
  }
}
