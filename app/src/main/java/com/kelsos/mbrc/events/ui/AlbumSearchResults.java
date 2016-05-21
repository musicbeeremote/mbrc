package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.Album;
import java.util.ArrayList;

public class AlbumSearchResults {
  private ArrayList<Album> list;
  private boolean stored;

  public AlbumSearchResults(ArrayList<Album> list, boolean stored) {
    this.list = list;
    this.stored = stored;
  }

  public ArrayList<Album> getList() {
    return this.list;
  }

  public boolean isStored() {
    return stored;
  }
}
