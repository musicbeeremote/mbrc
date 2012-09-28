package com.kelsos.mbrc.configuration;

import com.kelsos.mbrc.commands.RestartConnectionCommand;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.enums.UserInputEventType;

public class PreferenceViewCommandRegistration
{
	public static void register(Controller controller)
	{
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_SETTINGS_CHANGED, RestartConnectionCommand.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_SETTINGS_CHANGED);
	}

}
