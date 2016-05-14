package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.AlbumEntry;
import java.util.ArrayList;

public class AlbumSearchResults {
  private ArrayList<AlbumEntry> list;
  private boolean stored;

  public AlbumSearchResults(ArrayList<AlbumEntry> list, boolean stored) {
    this.list = list;
    this.stored = stored;
  }

  public ArrayList<AlbumEntry> getList() {
    return this.list;
  }

  public boolean isStored() {
    return stored;
  }
}
