package kelsos.mbremote.configuration;

import kelsos.mbremote.commands.PlayListDataAvailableCommand;
import kelsos.mbremote.commands.PlaySpecifiedTrackCommand;
import kelsos.mbremote.commands.RequestPlaylistCommand;
import kelsos.mbremote.controller.Controller;
import kelsos.mbremote.enums.ProtocolHandlerEventType;
import kelsos.mbremote.enums.UserInputEventType;

public class PlaylistViewCommandRegistration
{
	public static void register(Controller controller)
	{
		controller.registerCommand(UserInputEventType.Playlist, RequestPlaylistCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAYLIST_AVAILABLE, PlayListDataAvailableCommand.class);
		controller.registerCommand(UserInputEventType.PlaySpecifiedTrack, PlaySpecifiedTrackCommand.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unregisterCommand(UserInputEventType.Playlist);
		controller.unregisterCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAYLIST_AVAILABLE);
		controller.unregisterCommand(UserInputEventType.PlaySpecifiedTrack);
	}

}
