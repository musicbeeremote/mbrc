package com.kelsos.mbrc.core.networking.protocol.base

/**
 * Sealed class representing all protocol message types for the MusicBee Remote protocol.
 *
 * Protocols are categorized by their handling path:
 * - **Handshake:** Handled by [MessageHandler.handshake] during connection setup
 * - **Broadcast:** Server pushes state updates; handled by [CommandFactory] → [ProtocolAction]
 * - **Request/Response:** Client requests data; handled by [ApiBase] → [RequestManager]
 * - **Control:** Error and control messages handled specially by [MessageHandler]
 */
sealed class Protocol(val context: String) {

  // ===== HANDSHAKE PROTOCOLS =====
  // Handled by MessageHandler.handshake() during connection setup

  object Player : Protocol(PLAYER)
  object ProtocolTag : Protocol(PROTOCOL_TAG)
  object Init : Protocol(INIT)

  // ===== BROADCAST PROTOCOLS - Player State =====
  // Server pushes state updates; handled by CommandFactory → ProtocolActions

  object PluginVersion : Protocol(PLUGIN_VERSION)
  object PlayerStatus : Protocol(PLAYER_STATUS)
  object PlayerState : Protocol(PLAYER_STATE)
  object PlayerRepeat : Protocol(PLAYER_REPEAT)
  object PlayerShuffle : Protocol(PLAYER_SHUFFLE)
  object PlayerMute : Protocol(PLAYER_MUTE)
  object PlayerVolume : Protocol(PLAYER_VOLUME)
  object PlayerScrobble : Protocol(PLAYER_SCROBBLE)

  // ===== BROADCAST PROTOCOLS - Now Playing =====

  object NowPlayingTrack : Protocol(NOW_PLAYING_TRACK)
  object NowPlayingCover : Protocol(NOW_PLAYING_COVER)
  object NowPlayingPosition : Protocol(NOW_PLAYING_POSITION)
  object NowPlayingLyrics : Protocol(NOW_PLAYING_LYRICS)
  object NowPlayingRating : Protocol(NOW_PLAYING_RATING)
  object NowPlayingLfmRating : Protocol(NOW_PLAYING_LFM_RATING)
  object NowPlayingListChanged : Protocol(NOW_PLAYING_LIST_CHANGED)
  object NowPlayingListRemove : Protocol(NOW_PLAYING_LIST_REMOVE)
  object NowPlayingListMove : Protocol(NOW_PLAYING_LIST_MOVE)

  // ===== BROADCAST PROTOCOLS - Playback Control Responses =====

  object PlayerPlayPause : Protocol(PLAYER_PLAY_PAUSE)
  object PlayerPrevious : Protocol(PLAYER_PREVIOUS)
  object PlayerNext : Protocol(PLAYER_NEXT)
  object PlayerStop : Protocol(PLAYER_STOP)
  object PlayerPlay : Protocol(PLAYER_PLAY)
  object PlayerPause : Protocol(PLAYER_PAUSE)
  object NowPlayingListPlay : Protocol(NOW_PLAYING_LIST_PLAY)

  // ===== BROADCAST PROTOCOLS - Connection =====

  object Ping : Protocol(PING)
  object Pong : Protocol(PONG)

  // ===== REQUEST/RESPONSE PROTOCOLS =====
  // Client requests data; handled by ApiBase → RequestManager

  object NowPlayingList : Protocol(NOW_PLAYING_LIST)
  object NowPlayingQueue : Protocol(NOW_PLAYING_QUEUE)
  object PlaylistList : Protocol(PLAYLIST_LIST)
  object PlaylistPlay : Protocol(PLAYLIST_PLAY)
  object RadioStations : Protocol(RADIO_STATIONS)
  object LibraryBrowseGenres : Protocol(LIBRARY_BROWSE_GENRES)
  object LibraryBrowseArtists : Protocol(LIBRARY_BROWSE_ARTISTS)
  object LibraryBrowseAlbums : Protocol(LIBRARY_BROWSE_ALBUMS)
  object LibraryBrowseTracks : Protocol(LIBRARY_BROWSE_TRACKS)
  object LibraryCover : Protocol(LIBRARY_COVER)
  object LibraryPlayAll : Protocol(LIBRARY_PLAY_ALL)
  object PlayerOutput : Protocol(PLAYER_OUTPUT)
  object PlayerOutputSwitch : Protocol(PLAYER_OUTPUT_SWITCH)
  object VerifyConnection : Protocol(VERIFY_CONNECTION)

  // ===== CONTROL PROTOCOLS =====
  // Error and control messages handled specially by MessageHandler

  object ClientNotAllowed : Protocol(CLIENT_NOT_ALLOWED)
  object CommandUnavailable : Protocol(COMMAND_UNAVAILABLE)
  object UnknownCommand : Protocol(UNKNOWN_COMMAND)

  override fun toString(): String = context

  companion object {
    const val PLAYER = "player"
    const val PROTOCOL_TAG = "protocol"
    const val PLUGIN_VERSION = "pluginversion"
    const val CLIENT_NOT_ALLOWED = "notallowed"

    const val PLAYER_STATUS = "playerstatus"
    const val PLAYER_REPEAT = "playerrepeat"
    const val PLAYER_SCROBBLE = "scrobbler"
    const val PLAYER_SHUFFLE = "playershuffle"
    const val PLAYER_MUTE = "playermute"
    const val PLAYER_PLAY_PAUSE = "playerplaypause"
    const val PLAYER_PREVIOUS = "playerprevious"
    const val PLAYER_NEXT = "playernext"
    const val PLAYER_STOP = "playerstop"
    const val PLAYER_STATE = "playerstate"
    const val PLAYER_VOLUME = "playervolume"

    const val NOW_PLAYING_TRACK = "nowplayingtrack"
    const val NOW_PLAYING_COVER = "nowplayingcover"
    const val NOW_PLAYING_POSITION = "nowplayingposition"
    const val NOW_PLAYING_LYRICS = "nowplayinglyrics"
    const val NOW_PLAYING_RATING = "nowplayingrating"
    const val NOW_PLAYING_LFM_RATING = "nowplayinglfmrating"
    const val NOW_PLAYING_LIST = "nowplayinglist"
    const val NOW_PLAYING_LIST_PLAY = "nowplayinglistplay"
    const val NOW_PLAYING_LIST_REMOVE = "nowplayinglistremove"
    const val NOW_PLAYING_LIST_MOVE = "nowplayinglistmove"
    const val NOW_PLAYING_QUEUE = "nowplayingqueue"
    const val NOW_PLAYING_LIST_CHANGED = "nowplayinglistchanged"

    const val PING = "ping"
    const val PONG = "pong"
    const val INIT = "init"

    const val PLAYER_PLAY = "playerplay"
    const val PLAYER_PAUSE = "playerpause"

    const val PLAYLIST_LIST = "playlistlist"
    const val PLAYLIST_PLAY = "playlistplay"

    const val LIBRARY_BROWSE_GENRES = "browsegenres"
    const val LIBRARY_BROWSE_ARTISTS = "browseartists"
    const val LIBRARY_BROWSE_ALBUMS = "browsealbums"
    const val LIBRARY_BROWSE_TRACKS = "browsetracks"

    const val DISCOVERY = "discovery"

    const val VERIFY_CONNECTION = "verifyconnection"
    const val RADIO_STATIONS = "radiostations"

    const val COMMAND_UNAVAILABLE = "commandunavailable"

    const val PLAYER_OUTPUT = "playeroutput"
    const val PLAYER_OUTPUT_SWITCH = "playeroutputswitch"

    const val LIBRARY_COVER = "libraryalbumcover"
    const val LIBRARY_PLAY_ALL = "libraryplayall"

    /**
     * Toggle action in protocol. This should be send to the functions with multiple states
     * in order to change to the next in order state.
     */
    const val TOGGLE = "toggle"

    const val PROTOCOL_VERSION = 4

    const val UNKNOWN_COMMAND = "unknowncommand"

    private val contextMap: Map<String, Protocol> by lazy {
      mapOf(
        PLAYER to Player,
        PROTOCOL_TAG to ProtocolTag,
        PLUGIN_VERSION to PluginVersion,
        CLIENT_NOT_ALLOWED to ClientNotAllowed,
        PLAYER_STATUS to PlayerStatus,
        PLAYER_REPEAT to PlayerRepeat,
        PLAYER_SCROBBLE to PlayerScrobble,
        PLAYER_SHUFFLE to PlayerShuffle,
        PLAYER_MUTE to PlayerMute,
        PLAYER_PLAY_PAUSE to PlayerPlayPause,
        PLAYER_PREVIOUS to PlayerPrevious,
        PLAYER_NEXT to PlayerNext,
        PLAYER_STOP to PlayerStop,
        PLAYER_STATE to PlayerState,
        PLAYER_VOLUME to PlayerVolume,
        NOW_PLAYING_TRACK to NowPlayingTrack,
        NOW_PLAYING_COVER to NowPlayingCover,
        NOW_PLAYING_POSITION to NowPlayingPosition,
        NOW_PLAYING_LYRICS to NowPlayingLyrics,
        NOW_PLAYING_RATING to NowPlayingRating,
        NOW_PLAYING_LFM_RATING to NowPlayingLfmRating,
        NOW_PLAYING_LIST to NowPlayingList,
        NOW_PLAYING_LIST_PLAY to NowPlayingListPlay,
        NOW_PLAYING_LIST_REMOVE to NowPlayingListRemove,
        NOW_PLAYING_LIST_MOVE to NowPlayingListMove,
        NOW_PLAYING_QUEUE to NowPlayingQueue,
        NOW_PLAYING_LIST_CHANGED to NowPlayingListChanged,
        PING to Ping,
        PONG to Pong,
        INIT to Init,
        PLAYER_PLAY to PlayerPlay,
        PLAYER_PAUSE to PlayerPause,
        PLAYLIST_LIST to PlaylistList,
        PLAYLIST_PLAY to PlaylistPlay,
        LIBRARY_BROWSE_GENRES to LibraryBrowseGenres,
        LIBRARY_BROWSE_ARTISTS to LibraryBrowseArtists,
        LIBRARY_BROWSE_ALBUMS to LibraryBrowseAlbums,
        LIBRARY_BROWSE_TRACKS to LibraryBrowseTracks,
        VERIFY_CONNECTION to VerifyConnection,
        RADIO_STATIONS to RadioStations,
        COMMAND_UNAVAILABLE to CommandUnavailable,
        PLAYER_OUTPUT to PlayerOutput,
        PLAYER_OUTPUT_SWITCH to PlayerOutputSwitch,
        LIBRARY_COVER to LibraryCover,
        LIBRARY_PLAY_ALL to LibraryPlayAll
      )
    }

    fun fromString(context: String): Protocol = contextMap[context] ?: UnknownCommand
  }
}
