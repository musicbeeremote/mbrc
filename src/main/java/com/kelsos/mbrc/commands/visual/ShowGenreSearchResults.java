package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.GenreEntry;
import com.kelsos.mbrc.events.ui.GenreSearchResults;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;

public class ShowGenreSearchResults implements ICommand {
    @Inject MainThreadBusWrapper bus;

    @Override public void execute(IEvent e) {
        ArrayList<GenreEntry> genres = new ArrayList<GenreEntry>();
        ArrayNode node = (ArrayNode) e.getData();
        for (int i = 0; i < node.size(); i++) {
            JsonNode jNode = node.get(i);
            GenreEntry entry = new GenreEntry(jNode);
            genres.add(entry);
        }

        bus.post(new GenreSearchResults(genres));
    }
}
