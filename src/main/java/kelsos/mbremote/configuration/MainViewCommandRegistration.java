package kelsos.mbremote.configuration;

import kelsos.mbremote.commands.*;
import kelsos.mbremote.controller.Controller;
import kelsos.mbremote.enums.UserInputEventType;

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
		controller.unregisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_PLAY_PAUSE);
		controller.unregisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_STOP);
		controller.unregisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_NEXT);
		controller.unregisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_PREVIOUS);
		controller.unregisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_VOLUME);
		controller.unregisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_SHUFFLE);
		controller.unregisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_REPEAT);
		controller.unregisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_LAST_FM);
		controller.unregisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_MUTE);
		controller.unregisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_DATA_REFRESH);
		controller.unregisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_PLAYBACK_POSITION);
		controller.unregisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_INITIALIZE);
	}
}
