package com.kelsos.mbrc.commands.model;

import android.content.Context;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.db.LibraryDbHelper;
import com.kelsos.mbrc.data.dbdata.Playlist;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;

public class UpdateAvailablePlaylists implements ICommand {
    private LibraryDbHelper mHelper;

    @Inject public UpdateAvailablePlaylists(Context mContext) {
        mHelper = new LibraryDbHelper(mContext);
    }

    @Override public void execute(IEvent e) {
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        ArrayNode node = (ArrayNode) e.getData();
        for (int i = 0; i < node.size(); i++) {
            JsonNode jNode = node.get(i);
            Playlist entry = new Playlist(jNode);
            playlists.add(entry);
        }
        mHelper.batchInsertPlaylists(playlists);
    }
}
