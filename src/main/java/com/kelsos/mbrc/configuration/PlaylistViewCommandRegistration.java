package com.kelsos.mbrc.configuration;

import com.kelsos.mbrc.commands.PlayListDataAvailableCommand;
import com.kelsos.mbrc.commands.request.*;
import com.kelsos.mbrc.commands.visual.PlaylistViewTrackUpdatedCommand;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.events.ModelEvent;
import com.kelsos.mbrc.events.UserInputEvent;

public class PlaylistViewCommandRegistration
{
	public static void register(Controller controller)
	{
		controller.register(UserInputEvent.RequestNowPlayingList, NowPlayingList.class);
		controller.register(ModelEvent.ModelTrackUpdated, PlaylistViewTrackUpdatedCommand.class);
		controller.register(UserInputEvent.RequestNowPlayingRemoveTrack, RequestRemoveSelectedCommand.class);
        controller.register(UserInputEvent.RequestNowPlayingMoveTrack, RequestPlaylistReorderCommand.class);
        controller.register(UserInputEvent.RequestNowPlayingSearch, RequestNowPlayingSearch.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unregister(UserInputEvent.RequestNowPlayingList, NowPlayingList.class);
		controller.unregister(UserInputEvent.RequestNowPlayingPlayTrack, PlayListDataAvailableCommand.class);
		controller.unregister(ModelEvent.ModelTrackUpdated, PlaylistViewTrackUpdatedCommand.class);
		controller.unregister(UserInputEvent.RequestNowPlayingRemoveTrack, RequestRemoveSelectedCommand.class);
	}

}
