package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.ArtistEntry;
import com.kelsos.mbrc.events.ui.ArtistSearchResults;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;

public class ShowArtistSearchResults implements ICommand {
    @Inject MainThreadBusWrapper bus;

    @Override public void execute(IEvent e) {
        ArrayList<ArtistEntry> artists = new ArrayList<ArtistEntry>();
        ArrayNode node = (ArrayNode) e.getData();
        for (int i = 0; i < node.size(); i++) {
            JsonNode jNode = node.get(i);
            ArtistEntry entry = new ArtistEntry(jNode);
            artists.add(entry);
        }

        bus.post(new ArtistSearchResults(artists));
    }
}
