package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.LibraryDbHelper;
import com.kelsos.mbrc.model.MainDataModel;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

public class UpdateLibraryFiles implements ICommand {
    @Inject private MainDataModel model;

    @Override public void execute(IEvent e) {
        LibraryDbHelper dbHelper = model.getDbHelper();
        JsonNode node = (JsonNode) e.getData();
        ArrayNode array = (ArrayNode) node.path("files");
        for (int i = 0; i < array.size(); i++) {
            JsonNode jNode = array.get(i);
            dbHelper.createLibraryEntry(jNode.asText());
        }
    }
}
