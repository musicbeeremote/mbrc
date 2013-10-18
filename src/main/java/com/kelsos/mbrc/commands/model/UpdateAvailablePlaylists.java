package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Playlist;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;

public class UpdateAvailablePlaylists implements ICommand {
    private MainDataModel model;

    @Inject public UpdateAvailablePlaylists(MainDataModel model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        ArrayNode node = (ArrayNode) e.getData();
        for (int i = 0; i < node.size(); i++) {
            JsonNode jNode = node.get(i);
            Playlist entry = new Playlist(jNode);
            playlists.add(entry);
        }
        model.setAvailablePlaylists(playlists);
    }
}
