package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.Track;
import java.util.ArrayList;

public class TrackSearchResults {
  private ArrayList<Track> list;
  private boolean stored;

  public TrackSearchResults(ArrayList<Track> list, boolean stored) {
    this.list = list;
    this.stored = stored;
  }

  public ArrayList<Track> getList() {
    return this.list;
  }

  public boolean isStored() {
    return stored;
  }
}
