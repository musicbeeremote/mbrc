package com.kelsos.mbrc.commands.model;

import android.content.Context;
import com.google.inject.Inject;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.data.dbdata.Playlist;
import com.kelsos.mbrc.data.dbdata.PlaylistTrack;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.Protocol;
import com.squareup.otto.Bus;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatePlaylists implements ICommand {

    private Bus bus;

    @Inject public UpdatePlaylists(final Context context, final Bus bus) {

        this.bus = bus;
    }

    @Override public void execute(IEvent e) {

        JsonNode jNode = (JsonNode) e.getData();
        if (jNode.path("type").asText().equals("gettracks"))  {
            int offset = jNode.path("offset").asInt(0);
            int total = jNode.path("total").asInt(0);
            int limit = jNode.path("limit").asInt(0);
            String playlistHash = jNode.path("playlist_hash").asText();
            List<PlaylistTrack> tracks = new ArrayList<>();
            ArrayNode node = (ArrayNode) jNode.path("files");

            for (int i = 0; i < node.size(); i++) {
                JsonNode cNode = node.get(i);
                PlaylistTrack entry = new PlaylistTrack(cNode);
                //entry.setPlaylistHash(jNode.path("playlist_hash").asText());
                tracks.add(entry);
            }

    //        mHelper.batchInsertPlaylistTracks(tracks);

            if (offset + limit <= total) {
                Map<String, Object> message = new HashMap<>();
                message.put("type", "gettracks");
                message.put("playlist_hash", playlistHash);
                message.put("limit", limit);
                message.put("offset", offset + limit);

                bus.post(new MessageEvent(ProtocolEventType.USER_ACTION, new UserAction(Protocol.PLAYLISTS, message)));
            }
        } else if (jNode.path("type").asText().equals("get")) {
            ArrayList<Playlist> playlists = new ArrayList<>();
            ArrayNode node = (ArrayNode) jNode.path("playlists");
            for (int i = 0; i < node.size(); i++) {
                JsonNode cNode = node.get(i);
                Playlist entry = new Playlist(cNode);
                playlists.add(entry);
            }
      //      mHelper.batchInsertPlaylists(playlists);
        }


    }
}
