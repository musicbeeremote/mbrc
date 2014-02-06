package com.kelsos.mbrc.commands.model;

import android.util.Log;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.data.dbdata.NowPlayingTrack;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

public class UpdatePlaylists implements ICommand {



    @Inject public UpdatePlaylists(MainDataModel model) {

    }

    @Override public void execute(IEvent e) {
        JsonNode jNode = (JsonNode) e.getData();
        if (jNode.path("type").asText().equals("gettracks"))  {
            List<NowPlayingTrack> tracks = new ArrayList<>();
            ArrayNode node = (ArrayNode) jNode.path("files");
            for (int i = 0; i < node.size(); i++) {
                JsonNode cNode = node.get(i);
                NowPlayingTrack entry = new NowPlayingTrack(cNode);
                tracks.add(entry);
            }
            Log.d("received","total tracks:" + tracks.size());
        }

    }
}
