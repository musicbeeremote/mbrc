package com.kelsos.mbrc.commands;

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

public class NowPlayingListData implements ICommand {

    @Inject MainThreadBusWrapper bus;
    @Inject private MainDataModel model;

    @Override public void execute(final IEvent e) {
        int index = 0;

        ArrayNode node = (ArrayNode) e.getData();
        ArrayList<MusicTrack> playList = new ArrayList<MusicTrack>();
        String artist = model.getArtist();
        String title = model.getTitle();

        for (int i = 0; i < node.size(); i++) {
            JsonNode jNode = node.get(i);
            MusicTrack track = new MusicTrack(jNode);
            playList.add(track);
            if (track.getArtist().contains(artist) && track.getTitle().contains(title)) {
                index = i;
            }
        }

        bus.post(new NowPlayingListAvailable(playList, index));
    }
}
