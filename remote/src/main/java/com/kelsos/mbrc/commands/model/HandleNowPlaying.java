package com.kelsos.mbrc.commands.model;

import android.content.Context;
import android.util.Log;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.data.SyncHandler;
import com.kelsos.mbrc.data.db.LibraryDbHelper;
import com.kelsos.mbrc.data.dbdata.NowPlayingTrack;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

public class HandleNowPlaying implements ICommand {
    private static final String TAG = HandleNowPlaying.class.getCanonicalName();
    private MainDataModel model;
    private LibraryDbHelper mHelper;
    private SyncHandler mHandler;

    @Inject public HandleNowPlaying(MainDataModel model, Context context, SyncHandler mHandler) {
        this.model = model;
        this.mHelper = new LibraryDbHelper(context);
        this.mHandler = mHandler;
    }

    @Override public void execute(final IEvent e) {

        JsonNode node = (JsonNode) e.getData();
        String type = node.path("type").asText();

        if (type.equals("list")) {
            int limit = node.path("limit").asInt(50);
            int offset = node.path("offset").asInt(0);
            int total = node.path("total").asInt(0);
            ArrayNode listData = (ArrayNode) node.path("data");
            final int size = listData.size();
            List<NowPlayingTrack> list = new ArrayList<>();

            for(int i = 0; i < size; i++) {
                JsonNode jNode = listData.get(i);
                list.add(new NowPlayingTrack(jNode));
                mHelper.batchNowPlayingInsert(list);

            }

            if ((offset + limit) < total) {
                Log.d(TAG,"still more");
                mHandler.getQueueTracks(limit, offset + limit);
            } else {
                Log.d(TAG,"done");
            }

        }
    }
}
