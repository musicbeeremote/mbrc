package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.events.ui.NowPlayingListAvailable;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;

public class UpdateNowPlayingList implements ICommand {
    @Inject private MainDataModel model;

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
