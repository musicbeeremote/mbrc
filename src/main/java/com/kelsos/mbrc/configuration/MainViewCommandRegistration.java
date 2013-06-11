package com.kelsos.mbrc.configuration;

import com.kelsos.mbrc.commands.InitiateConnectionCommand;
import com.kelsos.mbrc.commands.RestartConnectionCommand;
import com.kelsos.mbrc.commands.request.*;
import com.kelsos.mbrc.commands.visual.*;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.events.ModelEvent;
import com.kelsos.mbrc.events.UserInputEvent;
import com.kelsos.mbrc.others.Protocol;

public class MainViewCommandRegistration
{
	public static void register(Controller controller)
	{
		controller.register(UserInputEvent.StartConnection, InitiateConnectionCommand.class);
		controller.register(UserInputEvent.ResetConnection, RestartConnectionCommand.class);
		controller.register(ModelEvent.ModelPlayStateUpdated, VisualUpdatePlaystateCommand.class);
        controller.register(Protocol.NowPlayingPosition, UpdatePlaybackPositionCommand.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unregister(UserInputEvent.StartConnection, InitiateConnectionCommand.class);
		controller.unregister(UserInputEvent.ResetConnection, RestartConnectionCommand.class);
		controller.unregister(ModelEvent.ModelPlayStateUpdated, VisualUpdatePlaystateCommand.class);
        controller.unregister(Protocol.NowPlayingPosition, UpdatePlaybackPositionCommand.class);
	}
}
