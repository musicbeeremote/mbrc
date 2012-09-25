package com.kelsos.mbrc.configuration;

import com.kelsos.mbrc.commands.*;
import com.kelsos.mbrc.commands.model.UpdateMainViewCommand;
import com.kelsos.mbrc.commands.request.*;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.enums.UserInputEventType;

public class MainViewCommandRegistration
{
	public static void register(Controller controller)
	{
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_PLAY_PAUSE, RequestPlayPauseCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_STOP, RequestPlayStopCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_NEXT, RequestPlayNextCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_PREVIOUS, RequestPlayPreviousCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_VOLUME, RequestVolumeChangeCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_SHUFFLE, RequestShuffleToggleCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_REPEAT, RequestRepeatToggleCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_LAST_FM, RequestScrobbleToggleCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_MUTE, RequestMuteToggleCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_DATA_REFRESH, UpdateMainViewCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_PLAYBACK_POSITION, RequestPlaybackPositionChangeCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_INITIALIZE, InitiateConnectionCommand.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_PLAY_PAUSE);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_STOP);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_NEXT);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_PREVIOUS);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_VOLUME);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_SHUFFLE);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_REPEAT);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_LAST_FM);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_MUTE);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_DATA_REFRESH);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_PLAYBACK_POSITION);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_INITIALIZE);
	}
}
