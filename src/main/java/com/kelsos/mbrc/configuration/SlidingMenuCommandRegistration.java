package com.kelsos.mbrc.configuration;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.request.RequestRatingChangeCommand;
import com.kelsos.mbrc.commands.visual.VisualUpdateRatingCommand;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.enums.ModelDataEventType;
import com.kelsos.mbrc.enums.UserInputEventType;

public class SlidingMenuCommandRegistration {

    @Inject
    public static void register(Controller controller){
        controller.registerCommand(ModelDataEventType.MODEL_RATING_UPDATED, VisualUpdateRatingCommand.class);
        controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_RATING_CHANGE, RequestRatingChangeCommand.class);
    }

    @Inject
    public static void unRegisterCommands(Controller controller){

    }
}
