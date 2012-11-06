package com.kelsos.mbrc.configuration;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.NotifyPluginOutOfDateCommand;
import com.kelsos.mbrc.commands.ProtocolReplyAvailableCommand;
import com.kelsos.mbrc.commands.ReduceVolumeOnRingCommand;
import com.kelsos.mbrc.commands.model.*;
import com.kelsos.mbrc.commands.visual.NotifyNotAllowedCommand;
import com.kelsos.mbrc.commands.visual.UpdatePlaybackPositionCommand;
import com.kelsos.mbrc.commands.visual.VisualUpdateHandshakeComplete;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.enums.ProtocolHandlerEventType;

public class ProtocolHandlerCommandRegistration
{
	@Inject
	public static void register(Controller controller)
	{
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_TITLE_AVAILABLE,UpdateTitleCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_ARTIST_AVAILABLE,UpdateArtistCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_ALBUM_AVAILABLE,UpdateAlbumCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_YEAR_AVAILABLE,UpdateYearCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_VOLUME_AVAILABLE,UpdateVolumeCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_COVER_AVAILABLE,UpdateAlbumCoverCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_REPEAT_STATE_AVAILABLE,UpdateRepeatStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_SHUFFLE_STATE_AVAILABLE,UpdateShuffleStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_SCROBBLE_STATE_AVAILABLE,UpdateScrobbleStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_MUTE_STATE_AVAILABLE,UpdateMuteStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAY_STATE_AVAILABLE,UpdatePlayStateCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAYBACK_POSITION_AVAILABLE,UpdatePlaybackPositionCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_REPLY_AVAILABLE,ProtocolReplyAvailableCommand.class);
		/* responsible for reducing the volume on incoming call */
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_REDUCE_VOLUME, ReduceVolumeOnRingCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_LYRICS_AVAILABLE, UpdateLyricsCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_HANDSHAKE_COMPLETE, VisualUpdateHandshakeComplete.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_NOT_ALLOWED, NotifyNotAllowedCommand.class);
		controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLUGIN_OUT_OF_DATE, NotifyPluginOutOfDateCommand.class);
	}

	@Inject
	public static void unRegister(Controller controller)
	{

	}
}
