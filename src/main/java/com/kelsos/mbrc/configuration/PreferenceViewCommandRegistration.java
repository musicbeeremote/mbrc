package com.kelsos.mbrc.configuration;

import com.kelsos.mbrc.commands.RestartConnectionCommand;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.events.UserInputEvent;

public class PreferenceViewCommandRegistration
{
	public static void register(Controller controller)
	{
		controller.register(UserInputEvent.SettingsChanged, RestartConnectionCommand.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unregister(UserInputEvent.SettingsChanged, RestartConnectionCommand.class);
	}

}
