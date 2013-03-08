package com.kelsos.mbrc.configuration;

import com.kelsos.mbrc.commands.visual.VisualUpdateLyricsCommand;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.events.ModelEvent;
import com.kelsos.mbrc.events.UserInputEvent;

public class LyricsViewCommandRegistration
{
	public static void register(Controller controller)
	{
		controller.register(UserInputEvent.RequestLyrics, VisualUpdateLyricsCommand.class);
	    controller.register(ModelEvent.ModelLyricsUpdated, VisualUpdateLyricsCommand.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unregister(UserInputEvent.RequestLyrics, VisualUpdateLyricsCommand.class);
		controller.unregister(ModelEvent.ModelLyricsUpdated, VisualUpdateLyricsCommand.class);
	}
}
