package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.TrackEntry;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;

public class UpdatePlaylistTracks implements ICommand {

    private MainDataModel model;

    @Inject public UpdatePlaylistTracks(MainDataModel model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        ArrayList<TrackEntry> tracks = new ArrayList<TrackEntry>();
        ArrayNode node = (ArrayNode) e.getData();
        for (int i = 0; i < node.size(); i++) {
            JsonNode jNode = node.get(i);
            TrackEntry entry = new TrackEntry(jNode);
            tracks.add(entry);
        }
        model.setPlaylistTracks(tracks);
    }
}
