package com.kelsos.mbrc.configuration;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.*;
import com.kelsos.mbrc.commands.model.*;
import com.kelsos.mbrc.commands.visual.NotifyNotAllowedCommand;
import com.kelsos.mbrc.commands.visual.UpdatePlaybackPositionCommand;
import com.kelsos.mbrc.commands.visual.VisualUpdateHandshakeComplete;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.events.ProtocolEvent;
import com.kelsos.mbrc.others.Protocol;

public class ProtocolHandlerCommandRegistration
{
	@Inject
	public static void register(Controller controller)
	{
		controller.register(ProtocolEvent.ReduceVolume, ReduceVolumeOnRingCommand.class);
		controller.register(ProtocolEvent.HandshakeComplete, VisualUpdateHandshakeComplete.class);
		controller.register(ProtocolEvent.InformClientNotAllowed, NotifyNotAllowedCommand.class);
		controller.register(ProtocolEvent.InformClientPluginOutOfDate, NotifyPluginOutOfDateCommand.class);
        controller.register(Protocol.NowPlayingTrack, UpdateNowPlayingTrack.class);
        controller.register(ProtocolEvent.InitiateProtocolRequest, ProtocolRequest.class);
        controller.register(Protocol.NowPlayingCover, UpdateCover.class);
        controller.register(Protocol.PlayerStatus, UpdatePlayerStatus.class);
        controller.register(Protocol.PlayerState, UpdatePlayState.class);
        controller.register(Protocol.PlayerRepeat, UpdateRepeat.class);
        controller.register(Protocol.PlayerVolume, UpdateVolume.class);
        controller.register(Protocol.PlayerMute, UpdateMute.class);
        controller.register(Protocol.PlayerShuffle, UpdateShuffle.class);
        controller.register(Protocol.PlayerScrobble, UpdateLastFm.class);
        controller.register(ProtocolEvent.NoSettingsAvailable, CreateSetupDialog.class);
        controller.register(Protocol.NowPlayingLyrics, UpdateLyrics.class);
        controller.register(ProtocolEvent.UserAction, ProcessUserAction.class);
	}

	@Inject
	public static void unRegister(Controller controller)
	{

	}
}
