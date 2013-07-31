package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import org.codehaus.jackson.node.ObjectNode;

public class UpdateNowPlayingTrack implements ICommand {
    @Inject MainDataModel model;

    @Override public void execute(IEvent e) {
        ObjectNode node = (ObjectNode) e.getData();
        model.setTrackInfo(node.path("Artist").getTextValue(),
                node.path("Album").getTextValue(),
                node.path("Title").getTextValue(),
                node.path("Year").getTextValue());
    }
}
