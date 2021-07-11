package com.kelsos.mbrc.networking.protocol

sealed class Protocol(val context: String) {
  object Player : Protocol(PLAYER)
  object ProtocolTag : Protocol(PROTOCOL_TAG)
  object PluginVersion : Protocol(PLUGIN_VERSION)
  object ClientNotAllowed : Protocol(CLIENT_NOT_ALLOWED)

  object PlayerStatus : Protocol(PLAYER_STATUS)
  object PlayerRepeat : Protocol(PLAYER_REPEAT)
  object PlayerScrobble : Protocol(PLAYER_SCROBBLE)
  object PlayerShuffle : Protocol(PLAYER_SHUFFLE)
  object PlayerMute : Protocol(PLAYER_MUTE)
  object PlayerPlayPause : Protocol(PLAYER_PLAYPAUSE)
  object PlayerPrevious : Protocol(PLAYER_PREVIOUS)
  object PlayerNext : Protocol(PLAYER_NEXT)
  object PlayerStop : Protocol(PLAYER_STOP)
  object PlayerState : Protocol(PLAYER_STATE)
  object PlayerVolume : Protocol(PLAYER_VOLUME)

  object NowPlayingTrack : Protocol(NOWPLAYING_TRACK)
  object NowPlayingCover : Protocol(NOWPLAYING_COVER)
  object NowPlayingPosition : Protocol(NOWPLAYING_POSITION)
  object NowPlayingLyrics : Protocol(NOWPLAYING_LYRICS)
  object NowPlayingRating : Protocol(NOWPLAYING_RATING)
  object NowPlayingLfmRating : Protocol(NOWPLAYING_LFMRATING)
  object NowPlayingList : Protocol(NOWPLAYING_LIST)
  object NowPlayingListPlay : Protocol(NOWPLAYING_LISTPLAY)
  object NowPlayingListRemove : Protocol(NOWPLAYING_LISTREMOVE)
  object NowPlayingListMove : Protocol(NOWPLAYING_LISTMOVE)
  object NowPlayingQueue : Protocol(NOWPLAYING_QUEUE)

  object Ping : Protocol(PING)
  object Pong : Protocol(PONG)
  object Init : Protocol(INIT)

  object PlayerPlay : Protocol(PLAYER_PLAY)
  object PlayerPause : Protocol(PLAYER_PAUSE)

  object PlaylistList : Protocol(PLAYLIST_LIST)
  object PlaylistPlay : Protocol(PLAYLIST_PLAY)

  object LibraryBrowseGenres : Protocol(LIBRARY_BROWSE_GENRES)
  object LibraryBrowseArtists : Protocol(LIBRARY_BROWSE_ARTISTS)
  object LibraryBrowseAlbums : Protocol(LIBRARY_BROWSE_ALBUMS)
  object LibraryBrowseTracks : Protocol(LIBRARY_BROWSE_TRACKS)

  object VerifyConnection : Protocol(VERIFY_CONNECTION)
  object RadioStations : Protocol(RADIO_STATIONS)

  object CommandUnavailable : Protocol(COMMAND_UNAVAILABLE)

  object PlayerOutput : Protocol(PLAYER_OUTPUT)
  object PlayerOutputSwitch : Protocol(PLAYER_OUTPUT_SWITCH)
  object UnknownCommand : Protocol(UNKNOWN_COMMAND)
  object LibraryCover : Protocol(LIBRARY_COVER)

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
    const val PLAYER_PLAYPAUSE = "playerplaypause"
    const val PLAYER_PREVIOUS = "playerprevious"
    const val PLAYER_NEXT = "playernext"
    const val PLAYER_STOP = "playerstop"
    const val PLAYER_STATE = "playerstate"
    const val PLAYER_VOLUME = "playervolume"

    const val NOWPLAYING_TRACK = "nowplayingtrack"
    const val NOWPLAYING_COVER = "nowplayingcover"
    const val NOWPLAYING_POSITION = "nowplayingposition"
    const val NOWPLAYING_LYRICS = "nowplayinglyrics"
    const val NOWPLAYING_RATING = "nowplayingrating"
    const val NOWPLAYING_LFMRATING = "nowplayinglfmrating"
    const val NOWPLAYING_LIST = "nowplayinglist"
    const val NOWPLAYING_LISTPLAY = "nowplayinglistplay"
    const val NOWPLAYING_LISTREMOVE = "nowplayinglistremove"
    const val NOWPLAYING_LISTMOVE = "nowplayinglistmove"
    const val NOWPLAYING_QUEUE = "nowplayingqueue"

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

    /**
     * Toggle action in protocol. This should be send to the functions with multiple states
     * in order to change to the next in order state.
     */
    const val TOGGLE = "toggle"

    const val ProtocolVersionNumber = 4

    const val UNKNOWN_COMMAND = "unknowncommand"

    fun fromString(context: String): Protocol = when (context) {
      PLAYER -> Player
      PROTOCOL_TAG -> ProtocolTag
      PLUGIN_VERSION -> PluginVersion
      CLIENT_NOT_ALLOWED -> ClientNotAllowed

      PLAYER_STATUS -> PlayerStatus
      PLAYER_REPEAT -> PlayerRepeat
      PLAYER_SCROBBLE -> PlayerScrobble
      PLAYER_SHUFFLE -> PlayerShuffle
      PLAYER_MUTE -> PlayerMute
      PLAYER_PLAYPAUSE -> PlayerPlayPause
      PLAYER_PREVIOUS -> PlayerPrevious
      PLAYER_NEXT -> PlayerNext
      PLAYER_STOP -> PlayerStop
      PLAYER_STATE -> PlayerState
      PLAYER_VOLUME -> PlayerVolume

      NOWPLAYING_TRACK -> NowPlayingTrack
      NOWPLAYING_COVER -> NowPlayingCover
      NOWPLAYING_POSITION -> NowPlayingPosition
      NOWPLAYING_LYRICS -> NowPlayingLyrics
      NOWPLAYING_RATING -> NowPlayingRating
      NOWPLAYING_LFMRATING -> NowPlayingLfmRating
      NOWPLAYING_LIST -> NowPlayingList
      NOWPLAYING_LISTPLAY -> NowPlayingListPlay
      NOWPLAYING_LISTREMOVE -> NowPlayingListRemove
      NOWPLAYING_LISTMOVE -> NowPlayingListMove
      NOWPLAYING_QUEUE -> NowPlayingQueue

      PING -> Ping
      PONG -> Pong
      INIT -> Init

      PLAYER_PLAY -> PlayerPlay
      PLAYER_PAUSE -> PlayerPause

      PLAYLIST_LIST -> PlaylistList
      PLAYLIST_PLAY -> PlaylistPlay

      LIBRARY_BROWSE_GENRES -> LibraryBrowseGenres
      LIBRARY_BROWSE_ARTISTS -> LibraryBrowseArtists
      LIBRARY_BROWSE_ALBUMS -> LibraryBrowseAlbums
      LIBRARY_BROWSE_TRACKS -> LibraryBrowseTracks

      VERIFY_CONNECTION -> VerifyConnection
      RADIO_STATIONS -> RadioStations

      COMMAND_UNAVAILABLE -> CommandUnavailable

      PLAYER_OUTPUT -> PlayerOutput
      PLAYER_OUTPUT_SWITCH -> PlayerOutputSwitch
      LIBRARY_COVER -> LibraryCover
      else -> UnknownCommand
    }
  }
}
