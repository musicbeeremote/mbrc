package kelsos.mbremote.configuration;

import kelsos.mbremote.commands.RequestLyricsCommand;
import kelsos.mbremote.commands.VisualUpdateLyricsCommand;
import kelsos.mbremote.controller.Controller;
import kelsos.mbremote.enums.ProtocolHandlerEventType;
import kelsos.mbremote.enums.UserInputEventType;

public class LyricsViewCommandRegistration
{
	public static void register(Controller controller)
	{
		controller.registerCommand(UserInputEventType.Lyrics, RequestLyricsCommand.class);
	    controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_LYRICS_AVAILABLE, VisualUpdateLyricsCommand.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unregisterCommand(UserInputEventType.Lyrics);
		controller.unregisterCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_LYRICS_AVAILABLE);
	}
}
