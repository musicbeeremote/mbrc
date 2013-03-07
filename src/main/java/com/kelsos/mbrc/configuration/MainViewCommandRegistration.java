package com.kelsos.mbrc.configuration;

import com.kelsos.mbrc.commands.InitiateConnectionCommand;
import com.kelsos.mbrc.commands.RestartConnectionCommand;
import com.kelsos.mbrc.commands.request.*;
import com.kelsos.mbrc.commands.visual.*;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.events.ModelEvent;
import com.kelsos.mbrc.events.UserInputEvent;

public class MainViewCommandRegistration
{
	public static void register(Controller controller)
	{
		controller.register(UserInputEvent.RequestPlayPause, RequestPlayPauseCommand.class);
		controller.register(UserInputEvent.RequestStop, RequestPlayStopCommand.class);
		controller.register(UserInputEvent.RequestNext, RequestPlayNextCommand.class);
		controller.register(UserInputEvent.RequestPrevious, RequestPlayPreviousCommand.class);
		controller.register(UserInputEvent.RequestVolume, RequestVolumeChangeCommand.class);
		controller.register(UserInputEvent.RequestShuffle, RequestShuffleToggleCommand.class);
		controller.register(UserInputEvent.RequestRepeat, RequestRepeatToggleCommand.class);
		controller.register(UserInputEvent.RequestScrobble, RequestScrobbleToggleCommand.class);
		controller.register(UserInputEvent.RequestMute, RequestMuteToggleCommand.class);
		controller.register(UserInputEvent.RequestMainViewUpdate, UpdateMainViewCommand.class);
		controller.register(UserInputEvent.RequestPosition, NowPlaygingPositionChange.class);
		controller.register(UserInputEvent.StartConnection, InitiateConnectionCommand.class);
		controller.register(UserInputEvent.ResetConnection, RestartConnectionCommand.class);
		controller.register(ModelEvent.ModelAlbumUpdated, VisualUpdateAlbumCommand.class);
		controller.register(ModelEvent.ModelArtistUpdated, VisualUpdateArtistCommand.class);
		controller.register(ModelEvent.ModelConnectionStateUpdated, VisualUpdateConnectionStateCommand.class);
		controller.register(ModelEvent.ModelCoverUpdated, VisualUpdateCoverCommand.class);
		controller.register(ModelEvent.ModelMuteStateUpdated, VisualUpdateMuteCommand.class);
		controller.register(ModelEvent.ModelPlayStateUpdated, VisualUpdatePlaystateCommand.class);
		controller.register(ModelEvent.ModelRepeatStateUpdated, VisualUpdateRepeatCommand.class);
		controller.register(ModelEvent.ModelScrobbleStateUpdated, VisualUpdateScrobbleCommand.class);
		controller.register(ModelEvent.ModelShuffleStateUpdated, VisualUpdateShuffleCommand.class);
		controller.register(ModelEvent.ModelTrackUpdated, VisualUpdateTitleCommand.class);
		controller.register(ModelEvent.ModelVolumeUpdated, VisualUpdateVolumeCommand.class);
		controller.register(ModelEvent.ModelYearUpdated, VisualUpdateYearTitleCommand.class);
		controller.register(ModelEvent.ModelCoverNotFound, NoCoverFoundCommand.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unRegisterCommand(UserInputEvent.RequestStop, RequestPlayStopCommand.class);
		controller.unRegisterCommand(UserInputEvent.RequestPrevious, RequestPlayPreviousCommand.class);
		controller.unRegisterCommand(UserInputEvent.RequestVolume, RequestVolumeChangeCommand.class);
		controller.unRegisterCommand(UserInputEvent.RequestShuffle, RequestShuffleToggleCommand.class);
		controller.unRegisterCommand(UserInputEvent.RequestRepeat, RequestRepeatToggleCommand.class);
		controller.unRegisterCommand(UserInputEvent.RequestScrobble, RequestScrobbleToggleCommand.class);
		controller.unRegisterCommand(UserInputEvent.RequestMute, RequestMuteToggleCommand.class);
		controller.unRegisterCommand(UserInputEvent.RequestMainViewUpdate, UpdateMainViewCommand.class);
		controller.unRegisterCommand(UserInputEvent.RequestPosition, NowPlaygingPositionChange.class);
		controller.unRegisterCommand(UserInputEvent.StartConnection, InitiateConnectionCommand.class);
		controller.unRegisterCommand(UserInputEvent.ResetConnection, RestartConnectionCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelAlbumUpdated, VisualUpdateAlbumCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelArtistUpdated, VisualUpdateArtistCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelConnectionStateUpdated, VisualUpdateConnectionStateCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelCoverUpdated, VisualUpdateCoverCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelMuteStateUpdated, VisualUpdateMuteCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelPlayStateUpdated, VisualUpdatePlaystateCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelRepeatStateUpdated, VisualUpdateRepeatCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelScrobbleStateUpdated, VisualUpdateScrobbleCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelShuffleStateUpdated, VisualUpdateShuffleCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelTrackUpdated, VisualUpdateTitleCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelVolumeUpdated, VisualUpdateVolumeCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelYearUpdated, VisualUpdateYearTitleCommand.class);
		controller.unRegisterCommand(ModelEvent.ModelCoverNotFound, NoCoverFoundCommand.class);
	}
}
