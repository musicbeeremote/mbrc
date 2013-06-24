package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.AlbumEntry;
import com.kelsos.mbrc.events.ui.AlbumSearchResults;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;

public class UpdateAlbumSearchResults implements ICommand {
    @Inject MainThreadBusWrapper bus;
    @Inject MainDataModel model;

    @Override public void execute(IEvent e) {

        ArrayList<AlbumEntry> albums = new ArrayList<AlbumEntry>();
        ArrayNode node = (ArrayNode) e.getData();
        for (int i = 0; i < node.size(); i++) {
            JsonNode jNode = node.get(i);
            AlbumEntry entry = new AlbumEntry(jNode);
            albums.add(entry);
        }
        model.setSearchAlbums(albums);
    }
}
