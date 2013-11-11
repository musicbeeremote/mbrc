package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.SyncHandler;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

public class HandleLibrarySync implements ICommand {
    private SyncHandler handler;

    @Inject public HandleLibrarySync(SyncHandler handler) {
        this.handler = handler;
    }

    @Override public void execute(IEvent e) {
        JsonNode node = (JsonNode) e.getData();
        String type = node.path("type").asText();

        if (type.equals("full")) {

            ArrayNode array = (ArrayNode) node.path("payload");
            for (int i = 0; i < array.size(); i++) {
                JsonNode jNode = array.get(i);
                handler.createEntry(jNode.asText());
            }

            handler.initFullSyncProcess();

        } else if (type.equals("partial")) {

        } else if (type.equals("cover")) {

        } else if (type.equals("meta")) {

        }
    }
}
