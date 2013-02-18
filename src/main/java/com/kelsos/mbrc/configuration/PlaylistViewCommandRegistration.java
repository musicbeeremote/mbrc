package com.kelsos.mbrc.configuration;

import com.kelsos.mbrc.commands.PlayListDataAvailableCommand;
import com.kelsos.mbrc.commands.request.*;
import com.kelsos.mbrc.commands.visual.PlaylistViewTrackUpdatedCommand;
import com.kelsos.mbrc.commands.visual.VisualUpdateTrackRemoved;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.enums.ModelDataEventType;
import com.kelsos.mbrc.enums.ProtocolHandlerEventType;
import com.kelsos.mbrc.enums.UserInputEventType;

public class PlaylistViewCommandRegistration
{
	public static void register(Controller controller)
	{
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_LIST, RequestPlaylistCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAYLIST_AVAILABLE, PlayListDataAvailableCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_PLAY_NOW, PlaySpecifiedTrackCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_TITLE_UPDATED, PlaylistViewTrackUpdatedCommand.class);
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_REMOVE, RequestRemoveSelectedCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAYLIST_TRACK_REMOVE,VisualUpdateTrackRemoved.class);
        controller.registerCommand(UserInputEventType.USERINPUT_EVENT_MOVE_NOWPLAYING_TRACK, RequestPlaylistReorderCommand.class);
        controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_NOW_PLAYING_SEARCH,RequestNowPlayingSearch.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_LIST, RequestPlaylistCommand.class);
		controller.unRegisterCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAYLIST_AVAILABLE, PlayListDataAvailableCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_PLAY_NOW, PlayListDataAvailableCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_TITLE_UPDATED, PlaylistViewTrackUpdatedCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_REMOVE, RequestRemoveSelectedCommand.class);
		controller.unRegisterCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAYLIST_TRACK_REMOVE,VisualUpdateTrackRemoved.class);
	}

}
