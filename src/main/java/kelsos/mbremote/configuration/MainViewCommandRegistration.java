package kelsos.mbremote.configuration;

import com.google.inject.Inject;
import kelsos.mbremote.Command.*;
import kelsos.mbremote.Controller.Controller;
import kelsos.mbremote.Enumerations.UserInputEventType;

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
		//controller.registerCommand(UserInputEventType.Lyrics, .class);
		controller.registerCommand(UserInputEventType.Refresh, UpdateMainViewCommand.class);
		controller.registerCommand(UserInputEventType.PlaybackPosition, RequestPlaybackPositionChangeCommand.class);
		//controller.registerCommand(UserInputEventType.Initialize, .class);
	}

	@Inject
	public static void unregisterCommands(Controller controller)
	{
		   controller.unregisterCommand(UserInputEventType.PlayPause);
	}
}
