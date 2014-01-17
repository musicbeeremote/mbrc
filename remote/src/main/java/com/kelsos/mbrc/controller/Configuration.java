package com.kelsos.mbrc.controller;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.*;
import com.kelsos.mbrc.commands.model.*;
import com.kelsos.mbrc.commands.visual.*;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.SocketEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.net.Protocol;

/**
 * Used to initialize the controller's configuration.
 */
public final class Configuration {

    private Configuration() { }

    /**
     * Initializes the mapping between events and commands for the passed controller.
     * @param controller The active controller.
     */
    @Inject public static void initialize(Controller controller) {
        controller.register(ProtocolEventType.REDUCE_VOLUME, ReduceVolumeOnRingCommand.class);
        controller.register(ProtocolEventType.HANDSHAKE_COMPLETE, VisualUpdateHandshakeComplete.class);
        controller.register(ProtocolEventType.INFORM_CLIENT_NOT_ALLOWED, NotifyNotAllowedCommand.class);
        controller.register(ProtocolEventType.INFORM_CLIENT_PLUGIN_OUT_OF_DATE, NotifyPluginOutOfDateCommand.class);
        controller.register(ProtocolEventType.INITIATE_PROTOCOL_REQUEST, ProtocolRequest.class);
        controller.register(ProtocolEventType.PLUGIN_VERSION_CHECK, VersionCheckCommand.class);
        controller.register(ProtocolEventType.USER_ACTION, ProcessUserAction.class);
        controller.register(Protocol.NOW_PLAYING_TRACK, UpdateNowPlayingTrack.class);
        controller.register(Protocol.NOW_PLAYING_COVER, UpdateCover.class);
        controller.register(Protocol.NOW_PLAYING_RATING, UpdateRating.class);
        controller.register(Protocol.PLAYER_STATUS, UpdatePlayerStatus.class);
        controller.register(Protocol.PLAYER_STATE, UpdatePlayState.class);
        controller.register(Protocol.PLAYER_REPEAT, UpdateRepeat.class);
        controller.register(Protocol.PLAYER_VOLUME, UpdateVolume.class);
        controller.register(Protocol.PLAYER_MUTE, UpdateMute.class);
        controller.register(Protocol.PLAYER_SHUFFLE, UpdateShuffle.class);
        controller.register(Protocol.PLAYER_SCROBBLE, UpdateLastFm.class);
        controller.register(Protocol.NOW_PLAYING_LYRICS, UpdateLyrics.class);
        controller.register(Protocol.NOW_PLAYING_LIST, UpdateNowPlayingList.class);
        controller.register(Protocol.NOW_PLAYING_LFM_RATING, UpdateLfmRating.class);
        controller.register(Protocol.NOW_PLAYING_REMOVE, UpdateNowPlayingTrackRemoval.class);
        controller.register(Protocol.NOW_PLAYING_MOVE, UpdateNowPlayingTrackMoved.class);
        controller.register(Protocol.LIBRARY_SEARCH_ARTIST, UpdateArtistSearchResults.class);
        controller.register(Protocol.LIBRARY_SEARCH_ALBUM, UpdateAlbumSearchResults.class);
        controller.register(Protocol.LIBRARY_SEARCH_GENRE, UpdateGenreSearchResults.class);
        controller.register(Protocol.LIBRARY_SEARCH_TITLE, UpdateTrackSearchResults.class);
        controller.register(Protocol.LIBRARY_ARTIST_ALBUMS, UpdateAlbumSearchResults.class);
        controller.register(Protocol.LIBRARY_GENRE_ARTISTS, UpdateArtistSearchResults.class);
        controller.register(Protocol.LIBRARY_ALBUM_TRACKS, UpdateTrackSearchResults.class);
        controller.register(Protocol.NOW_PLAYING_POSITION, UpdatePlaybackPositionCommand.class);
        controller.register(Protocol.PLUGIN_VERSION, UpdatePluginVersionCommand.class);
        controller.register(Protocol.PLAYLIST_LIST, UpdateAvailablePlaylists.class);
        controller.register(Protocol.PLAYLIST_GET_FILES, UpdatePlaylistTracks.class);
        controller.register(Protocol.LIBRARY_SYNC, HandleLibrarySync.class);

        controller.register(UserInputEventType.SETTINGS_CHANGED, RestartConnectionCommand.class);
        controller.register(UserInputEventType.CANCEL_NOTIFICATION, CancelNotificationCommand.class);
        controller.register(UserInputEventType.START_CONNECTION, InitiateConnectionCommand.class);
        controller.register(UserInputEventType.RESET_CONNECTION, RestartConnectionCommand.class);
        controller.register(UserInputEventType.START_DISCOVERY, StartDiscoveryCommand.class);
        controller.register(UserInputEventType.KEY_VOLUME_UP, KeyVolumeUpCommand.class);
        controller.register(UserInputEventType.KEY_VOLUME_DOWN, KeyVolumeDownCommand.class);
        controller.register(SocketEventType.DATA_AVAILABLE, SocketDataAvailableCommand.class);
        controller.register(SocketEventType.STATUS_CHANGED, ConnectionStatusChangedCommand.class);
        controller.register(SocketEventType.HANDSHAKE_UPDATE, HandleHanshake.class);
    }
}
