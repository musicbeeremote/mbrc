package com.kelsos.mbrc.configuration;

import com.kelsos.mbrc.commands.InitiateConnectionCommand;
import com.kelsos.mbrc.commands.RestartConnectionCommand;
import com.kelsos.mbrc.commands.request.*;
import com.kelsos.mbrc.commands.visual.*;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.events.ModelEvent;
import com.kelsos.mbrc.events.UserInputEvent;
import com.kelsos.mbrc.others.Protocol;

public class MainViewCommandRegistration
{
	public static void register(Controller controller)
	{
        controller.register(UserInputEvent.RequestMainViewUpdate, UpdateMainViewCommand.class);
		controller.register(UserInputEvent.StartConnection, InitiateConnectionCommand.class);
		controller.register(UserInputEvent.ResetConnection, RestartConnectionCommand.class);
		controller.register(ModelEvent.ModelConnectionStateUpdated, VisualUpdateConnectionStateCommand.class);
		controller.register(ModelEvent.ModelCoverUpdated, VisualUpdateCoverCommand.class);
		controller.register(ModelEvent.ModelMuteStateUpdated, VisualUpdateMuteCommand.class);
		controller.register(ModelEvent.ModelPlayStateUpdated, VisualUpdatePlaystateCommand.class);
		controller.register(ModelEvent.ModelRepeatStateUpdated, VisualUpdateRepeatCommand.class);
		controller.register(ModelEvent.ModelScrobbleStateUpdated, VisualUpdateScrobbleCommand.class);
		controller.register(ModelEvent.ModelShuffleStateUpdated, VisualUpdateShuffleCommand.class);
		controller.register(ModelEvent.ModelTrackUpdated, VisualUpdateTrackInfo.class);
		controller.register(ModelEvent.ModelVolumeUpdated, VisualUpdateVolumeCommand.class);
		controller.register(ModelEvent.ModelCoverNotFound, NoCoverFoundCommand.class);
        controller.register(Protocol.NowPlayingPosition, UpdatePlaybackPositionCommand.class);
        controller.register(ModelEvent.ModelRatingUpdated, VisualUpdateRatingCommand.class);
	}

	public static void unRegister(Controller controller)
	{
        controller.unregister(UserInputEvent.RequestMainViewUpdate, UpdateMainViewCommand.class);
		controller.unregister(UserInputEvent.StartConnection, InitiateConnectionCommand.class);
		controller.unregister(UserInputEvent.ResetConnection, RestartConnectionCommand.class);
		controller.unregister(ModelEvent.ModelConnectionStateUpdated, VisualUpdateConnectionStateCommand.class);
		controller.unregister(ModelEvent.ModelCoverUpdated, VisualUpdateCoverCommand.class);
		controller.unregister(ModelEvent.ModelMuteStateUpdated, VisualUpdateMuteCommand.class);
		controller.unregister(ModelEvent.ModelPlayStateUpdated, VisualUpdatePlaystateCommand.class);
		controller.unregister(ModelEvent.ModelRepeatStateUpdated, VisualUpdateRepeatCommand.class);
		controller.unregister(ModelEvent.ModelScrobbleStateUpdated, VisualUpdateScrobbleCommand.class);
		controller.unregister(ModelEvent.ModelShuffleStateUpdated, VisualUpdateShuffleCommand.class);
		controller.unregister(ModelEvent.ModelTrackUpdated, VisualUpdateTrackInfo.class);
		controller.unregister(ModelEvent.ModelVolumeUpdated, VisualUpdateVolumeCommand.class);
		controller.unregister(ModelEvent.ModelCoverNotFound, NoCoverFoundCommand.class);
        controller.unregister(Protocol.NowPlayingPosition, UpdatePlaybackPositionCommand.class);
        controller.unregister(ModelEvent.ModelRatingUpdated, VisualUpdateRatingCommand.class);
	}
}
