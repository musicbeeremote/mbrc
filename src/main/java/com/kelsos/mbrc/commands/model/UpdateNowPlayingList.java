package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.model.MusicTrack;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;

public class UpdateNowPlayingList implements ICommand {
    private MainDataModel model;

    @Inject public UpdateNowPlayingList(MainDataModel model) {
        this.model = model;
    }

    @Override public void execute(final IEvent e) {
        ArrayNode node = (ArrayNode) e.getData();
        ArrayList<MusicTrack> playList = new ArrayList<MusicTrack>();
        for (int i = 0; i < node.size(); i++) {
            JsonNode jNode = node.get(i);
            playList.add(new MusicTrack(jNode));
        }

        model.setNowPlayingList(playList);
    }
}
