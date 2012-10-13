package com.kelsos.mbrc.configuration;

import com.kelsos.mbrc.commands.visual.VisualUpdateLyricsCommand;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.enums.ModelDataEventType;
import com.kelsos.mbrc.enums.UserInputEventType;

public class LyricsViewCommandRegistration
{
	public static void register(Controller controller)
	{
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_LYRICS, VisualUpdateLyricsCommand.class);
	    controller.registerCommand(ModelDataEventType.MODEL_LYRICS_UPDATED, VisualUpdateLyricsCommand.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_LYRICS, VisualUpdateLyricsCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_LYRICS_UPDATED, VisualUpdateLyricsCommand.class);
	}
}
