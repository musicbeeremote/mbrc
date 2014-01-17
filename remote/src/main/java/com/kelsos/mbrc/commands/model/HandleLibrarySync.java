package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.SyncHandler;
import com.kelsos.mbrc.data.dbdata.Track;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
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
            int totalFiles = node.path("payload").asInt();
            handler.initFullSyncProcess(totalFiles);

        } else if (type.equals("partial")) {

        } else if (type.equals("cover")) {
            JsonNode payload = node.path("payload");
            String sha1 = payload.path("hash").asText();
            String image = payload.path("image").asText();
            handler.updateCover(image, sha1);
        } else if (type.equals("meta")) {
            ArrayNode trackNode = (ArrayNode) node.path("data");
            for (int i = 0; i < node.size(); i++) {
                JsonNode jNode = trackNode.get(i);
                Track track = new Track(jNode);
                handler.createEntry(track);
            }
            handler.getNextBunch();
        }
    }
}
