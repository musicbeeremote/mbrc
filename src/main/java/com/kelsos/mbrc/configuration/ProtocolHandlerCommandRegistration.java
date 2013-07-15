package com.kelsos.mbrc.configuration;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.*;
import com.kelsos.mbrc.commands.model.*;
import com.kelsos.mbrc.commands.visual.*;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.events.ProtocolEvent;
import com.kelsos.mbrc.events.UserInputEvent;
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
        controller.register(ProtocolEvent.InitiateProtocolRequest, ProtocolRequest.class);
        controller.register(ProtocolEvent.UserAction, ProcessUserAction.class);
        controller.register(Protocol.NowPlayingTrack, UpdateNowPlayingTrack.class);
        controller.register(Protocol.NowPlayingCover, UpdateCover.class);
        controller.register(Protocol.NowPlayingRating, UpdateRating.class);
        controller.register(Protocol.PlayerStatus, UpdatePlayerStatus.class);
        controller.register(Protocol.PlayerState, UpdatePlayState.class);
        controller.register(Protocol.PlayerRepeat, UpdateRepeat.class);
        controller.register(Protocol.PlayerVolume, UpdateVolume.class);
        controller.register(Protocol.PlayerMute, UpdateMute.class);
        controller.register(Protocol.PlayerShuffle, UpdateShuffle.class);
        controller.register(Protocol.PlayerScrobble, UpdateLastFm.class);
        controller.register(Protocol.NowPlayingLyrics, UpdateLyrics.class);
        controller.register(Protocol.NowPlayingList, UpdateNowPlayingList.class);
        controller.register(Protocol.LibrarySearchArtist, UpdateArtistSearchResults.class);
        controller.register(Protocol.LibrarySearchAlbum, UpdateAlbumSearchResults.class);
        controller.register(Protocol.LibrarySearchGenre, UpdateGenreSearchResults.class);
        controller.register(Protocol.LibrarySearchTitle, UpdateTrackSearchResults.class);
        controller.register(Protocol.LibraryArtistAlbums, UpdateAlbumSearchResults.class);
        controller.register(Protocol.LibraryGenreArtists, UpdateArtistSearchResults.class);
        controller.register(Protocol.LibraryAlbumTracks, UpdateTrackSearchResults.class);
        controller.register(Protocol.NowPlayingPosition, UpdatePlaybackPositionCommand.class);
        controller.register(UserInputEvent.StartConnection, InitiateConnectionCommand.class);
        controller.register(UserInputEvent.ResetConnection, RestartConnectionCommand.class);
	}

	@Inject
	public static void unRegister(Controller controller)
	{

	}
}
