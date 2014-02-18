package com.kelsos.mbrc.commands.model;

import android.util.Log;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.data.SyncHandler;
import com.kelsos.mbrc.data.dbdata.Track;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

public class HandleLibrarySync implements ICommand {
    private SyncHandler handler;

    @Inject public HandleLibrarySync(SyncHandler handler) {
        this.handler = handler;
    }

    @Override public void execute(IEvent e) {
        JsonNode node = (JsonNode) e.getData();
        String type = node.path("type").asText();

        if (type.equals("cover")) {
            JsonNode payload = node.path("payload");
            String sha1 = payload.path("hash").asText();
            String image = payload.path("image").asText();
            handler.updateCover(image, sha1);
        } else if (type.equals("meta")) {
            int limit = node.path("limit").asInt(50);
            int offset = node.path("offset").asInt(0);
            int total = node.path("total").asInt(0);

            ArrayNode trackNode = (ArrayNode) node.path("data");
            List<Track> list = new ArrayList<>();
            for (int i = 0; i < trackNode.size(); i++) {
                JsonNode jNode = trackNode.get(i);
                list.add(new Track(jNode));
            }

            handler.processBatch(list);

            Log.d(BuildConfig.PACKAGE_NAME, "Processing batch of " + list.size());

            handler.getNextBatch(total, offset, limit);
        }
    }
}
