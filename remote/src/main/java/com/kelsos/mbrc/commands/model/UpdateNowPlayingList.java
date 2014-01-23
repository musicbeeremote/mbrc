package com.kelsos.mbrc.commands.model;

import android.content.Context;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.data.db.LibraryDbHelper;
import com.kelsos.mbrc.data.dbdata.NowPlayingTrack;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

public class UpdateNowPlayingList implements ICommand {
    private MainDataModel model;
    private LibraryDbHelper mHelper;

    @Inject public UpdateNowPlayingList(MainDataModel model, Context context) {
        this.model = model;
        this.mHelper = new LibraryDbHelper(context);
    }

    @Override public void execute(final IEvent e) {
        ArrayNode node = (ArrayNode) e.getData();
        List<NowPlayingTrack> playList = new ArrayList<>();
        for (int i = 0; i < node.size(); i++) {
            JsonNode jNode = node.get(i);
            playList.add(new NowPlayingTrack(jNode));
        }

        model.setNowPlayingList(playList);
        mHelper.batchNowPlayingInsert(playList);

    }
}
