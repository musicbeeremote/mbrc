package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.TrackEntry;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import java.util.ArrayList;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

public class UpdateTrackSearchResults implements ICommand {
  private MainDataModel model;

  @Inject public UpdateTrackSearchResults(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {

    ArrayList<TrackEntry> tracks = new ArrayList<>();
    ArrayNode node = (ArrayNode) e.getData();
    for (int i = 0; i < node.size(); i++) {
      JsonNode jNode = node.get(i);
      TrackEntry entry = new TrackEntry(jNode);
      tracks.add(entry);
    }
    model.setSearchTracks(tracks);
  }
}
