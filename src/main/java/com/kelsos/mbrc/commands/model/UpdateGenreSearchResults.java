package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.GenreEntry;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;

public class UpdateGenreSearchResults implements ICommand {
    private MainDataModel model;

    @Inject public UpdateGenreSearchResults(MainDataModel model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        ArrayList<GenreEntry> genres = new ArrayList<GenreEntry>();
        ArrayNode node = (ArrayNode) e.getData();
        for (int i = 0; i < node.size(); i++) {
            JsonNode jNode = node.get(i);
            GenreEntry entry = new GenreEntry(jNode);
            genres.add(entry);
        }
        model.setSearchGenres(genres);
    }
}
