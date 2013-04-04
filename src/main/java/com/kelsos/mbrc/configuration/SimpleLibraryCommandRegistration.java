package com.kelsos.mbrc.configuration;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.request.RequestLfmBan;
import com.kelsos.mbrc.commands.request.RequestLfmLove;
import com.kelsos.mbrc.commands.request.RequestRatingChangeCommand;
import com.kelsos.mbrc.commands.visual.ShowArtistSearchResults;
import com.kelsos.mbrc.commands.visual.VisualUpdateRatingCommand;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.events.ModelEvent;
import com.kelsos.mbrc.events.UserInputEvent;
import com.kelsos.mbrc.others.Protocol;

public class SimpleLibraryCommandRegistration {

    @Inject
    public static void register(Controller controller){
         controller.register(Protocol.LibrarySearchArtist, ShowArtistSearchResults.class);
    }

    @Inject
    public static void unRegister(Controller controller){
        controller.unregister(Protocol.LibrarySearchArtist, ShowArtistSearchResults.class);
    }
}
