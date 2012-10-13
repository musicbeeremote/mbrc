package com.kelsos.mbrc.configuration;

import com.kelsos.mbrc.commands.InitiateConnectionCommand;
import com.kelsos.mbrc.commands.RestartConnectionCommand;
import com.kelsos.mbrc.commands.request.*;
import com.kelsos.mbrc.commands.visual.*;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.enums.ModelDataEventType;
import com.kelsos.mbrc.enums.UserInputEventType;

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
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_CONNECTION_RESET, RestartConnectionCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_ALBUM_UPDATED, VisualUpdateAlbumCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_ARTIST_UPDATED, VisualUpdateArtistCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_CONNECTION_STATE_UPDATED, VisualUpdateConnectionStateCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_COVER_UPDATED, VisualUpdateCoverCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_MUTE_STATE_UPDATED, VisualUpdateMuteCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_PLAY_STATE_UPDATED, VisualUpdatePlaystateCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_REPEAT_STATE_UPDATED, VisualUpdateRepeatCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_SCROBBLE_STATE_UPDATED, VisualUpdateScrobbleCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_SHUFFLE_STATE_UPDATED, VisualUpdateShuffleCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_TITLE_UPDATED, VisualUpdateTitleCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_VOLUME_UPDATED, VisualUpdateVolumeCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_YEAR_UPDATED, VisualUpdateYearTitleCommand.class);
		controller.registerCommand(ModelDataEventType.MODEL_COVER_NOT_FOUND, NoCoverFoundCommand.class);
	}

	public static void unRegister(Controller controller)
	{
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_PLAY_PAUSE, RequestPlayPauseCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_STOP, RequestPlayStopCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_NEXT, RequestPlayNextCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_PREVIOUS, RequestPlayPreviousCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_VOLUME, RequestVolumeChangeCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_SHUFFLE, RequestShuffleToggleCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_REPEAT, RequestRepeatToggleCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_LAST_FM, RequestScrobbleToggleCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_MUTE, RequestMuteToggleCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_DATA_REFRESH, UpdateMainViewCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_PLAYBACK_POSITION, RequestPlaybackPositionChangeCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_INITIALIZE, InitiateConnectionCommand.class);
		controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_REQUEST_CONNECTION_RESET, RestartConnectionCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_ALBUM_UPDATED, VisualUpdateAlbumCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_ARTIST_UPDATED, VisualUpdateArtistCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_CONNECTION_STATE_UPDATED, VisualUpdateConnectionStateCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_COVER_UPDATED, VisualUpdateCoverCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_MUTE_STATE_UPDATED, VisualUpdateMuteCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_PLAY_STATE_UPDATED, VisualUpdatePlaystateCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_REPEAT_STATE_UPDATED, VisualUpdateRepeatCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_SCROBBLE_STATE_UPDATED, VisualUpdateScrobbleCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_SHUFFLE_STATE_UPDATED, VisualUpdateShuffleCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_TITLE_UPDATED, VisualUpdateTitleCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_VOLUME_UPDATED, VisualUpdateVolumeCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_YEAR_UPDATED, VisualUpdateYearTitleCommand.class);
		controller.unRegisterCommand(ModelDataEventType.MODEL_COVER_NOT_FOUND, NoCoverFoundCommand.class);
	}
}
