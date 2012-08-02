package kelsos.mbremote.configuration;

import com.google.inject.Inject;
import kelsos.mbremote.commands.*;
import kelsos.mbremote.controller.Controller;
import kelsos.mbremote.enums.UserInputEventType;

public class MainViewCommandRegistration
{
	@Inject
	public static void registerCommands(Controller controller)
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
		//controller.registerCommand(UserInputEventType.PROTOCOL_HANDLER_LYRICS_AVAILABLE, .class);
		controller.registerCommand(UserInputEventType.Refresh, UpdateMainViewCommand.class);
		controller.registerCommand(UserInputEventType.PlaybackPosition, RequestPlaybackPositionChangeCommand.class);
		controller.registerCommand(UserInputEventType.Initialize, InitiateConnectionCommand.class);
	}

	@Inject
	public static void unregisterCommands(Controller controller)
	{
		   controller.unregisterCommand(UserInputEventType.PlayPause);
	}
}
