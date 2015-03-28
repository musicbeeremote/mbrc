package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.ArtistEntry;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import java.util.ArrayList;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

public class UpdateArtistSearchResults implements ICommand {
  private MainDataModel model;

  @Inject public UpdateArtistSearchResults(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    ArrayList<ArtistEntry> artists = new ArrayList<>();
    ArrayNode node = (ArrayNode) e.getData();
    for (int i = 0; i < node.size(); i++) {
      JsonNode jNode = node.get(i);
      ArtistEntry entry = new ArtistEntry(jNode);
      artists.add(entry);
    }
    model.setSearchArtists(artists);
  }
}
