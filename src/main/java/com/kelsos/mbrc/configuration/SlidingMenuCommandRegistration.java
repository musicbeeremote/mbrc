package com.kelsos.mbrc.configuration;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.request.RequestLfmBan;
import com.kelsos.mbrc.commands.request.RequestLfmLove;
import com.kelsos.mbrc.commands.request.RequestRatingChangeCommand;
import com.kelsos.mbrc.commands.visual.VisualUpdateRatingCommand;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.events.ModelEvent;
import com.kelsos.mbrc.events.UserInputEvent;

public class SlidingMenuCommandRegistration {

    @Inject
    public static void register(Controller controller){
        controller.register(UserInputEvent.RequestLastFmBan, RequestLfmBan.class);
        controller.register(UserInputEvent.RequestLastFmLove, RequestLfmLove.class);
    }

    @Inject
    public static void unRegisterCommands(Controller controller){

    }
}
