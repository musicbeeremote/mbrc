package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.LibraryDbHelper;
import com.kelsos.mbrc.model.LibraryTrack;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.model.UserAction;
import com.kelsos.mbrc.net.Protocol;
import com.squareup.otto.Bus;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.List;

public class HandleLibrarySync implements ICommand {
    @Inject private MainDataModel model;
    @Inject private Bus bus;

    @Override public void execute(IEvent e) {
        JsonNode node = (JsonNode) e.getData();
        String type = node.path("type").asText();

        if (type.equals("full")) {
            LibraryDbHelper dbHelper = model.getDbHelper();

            ArrayNode array = (ArrayNode) node.path("files");
            for (int i = 0; i < array.size(); i++) {
                JsonNode jNode = array.get(i);
                dbHelper.createLibraryEntry(jNode.asText());
            }

        } else if (type.equals("update")) {

        } else if (type.equals("cover")) {

        }
    }
}
