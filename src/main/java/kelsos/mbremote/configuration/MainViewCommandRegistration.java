package kelsos.mbremote.configuration;

import kelsos.mbremote.commands.*;
import kelsos.mbremote.controller.Controller;
import kelsos.mbremote.enums.UserInputEventType;

public class MainViewCommandRegistration
{
	public static void register(Controller controller)
	{
		controller.registerCommand(UserInputEventType.PlayPause, RequestPlayPauseCommand.class);
		controller.registerCommand(UserInputEventType.Stop, RequestPlayStopCommand.class);
		controller.registerCommand(UserInputEventType.Next, RequestPlayNextCommand.class);
		controller.registerCommand(UserInputEventType.Previous, RequestPlayPreviousCommand.class);
		controller.registerCommand(UserInputEventType.Volume, RequestVolumeChangeCommand.class);
		controller.registerCommand(UserInputEventType.Shuffle, RequestShuffleToggleCommand.class);
		controller.registerCommand(UserInputEventType.Repeat, RequestRepeatToggleCommand.class);
		controller.registerCommand(UserInputEventType.Scrobble, RequestScrobbleToggleCommand.class);
		controller.registerCommand(UserInputEventType.Mute, RequestMuteToggleCommand.class);
		controller.registerCommand(UserInputEventType.Refresh, UpdateMainViewCommand.class);
		controller.registerCommand(UserInputEventType.PlaybackPosition, RequestPlaybackPositionChangeCommand.class);
		controller.registerCommand(UserInputEventType.Initialize, InitiateConnectionCommand.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unregisterCommand(UserInputEventType.PlayPause);
		controller.unregisterCommand(UserInputEventType.Stop);
		controller.unregisterCommand(UserInputEventType.Next);
		controller.unregisterCommand(UserInputEventType.Previous);
		controller.unregisterCommand(UserInputEventType.Volume);
		controller.unregisterCommand(UserInputEventType.Shuffle);
		controller.unregisterCommand(UserInputEventType.Repeat);
		controller.unregisterCommand(UserInputEventType.Scrobble);
		controller.unregisterCommand(UserInputEventType.Mute);
		controller.unregisterCommand(UserInputEventType.Refresh);
		controller.unregisterCommand(UserInputEventType.PlaybackPosition);
		controller.unregisterCommand(UserInputEventType.Initialize);
	}
}
