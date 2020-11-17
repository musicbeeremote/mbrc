package com.kelsos.mbrc.networking.protocol

import androidx.annotation.StringDef

object Protocol {
  const val Player = "player"
  const val ProtocolTag = "protocol"
  const val PluginVersion = "pluginversion"
  const val ClientNotAllowed = "notallowed"

  const val PlayerStatus = "playerstatus"
  const val PlayerRepeat = "playerrepeat"
  const val PlayerScrobble = "scrobbler"
  const val PlayerShuffle = "playershuffle"
  const val PlayerMute = "playermute"
  const val PlayerPlayPause = "playerplaypause"
  const val PlayerPrevious = "playerprevious"
  const val PlayerNext = "playernext"
  const val PlayerStop = "playerstop"
  const val PlayerState = "playerstate"
  const val PlayerVolume = "playervolume"

  const val NowPlayingTrack = "nowplayingtrack"
  const val NowPlayingCover = "nowplayingcover"
  const val NowPlayingPosition = "nowplayingposition"
  const val NowPlayingLyrics = "nowplayinglyrics"
  const val NowPlayingRating = "nowplayingrating"
  const val NowPlayingLfmRating = "nowplayinglfmrating"
  const val NowPlayingList = "nowplayinglist"
  const val NowPlayingListPlay = "nowplayinglistplay"
  const val NowPlayingListRemove = "nowplayinglistremove"
  const val NowPlayingListMove = "nowplayinglistmove"
  const val NowPlayingQueue = "nowplayingqueue"

  const val PING = "ping"
  const val PONG = "pong"
  const val INIT = "init"

  const val PlayerPlay = "playerplay"
  const val PlayerPause = "playerpause"

  const val PlaylistList = "playlistlist"
  const val PlaylistPlay = "playlistplay"
  const val NoBroadcast = "nobroadcast"

  const val LibraryBrowseGenres = "browsegenres"
  const val LibraryBrowseArtists = "browseartists"
  const val LibraryBrowseAlbums = "browsealbums"
  const val LibraryBrowseTracks = "browsetracks"

  const val DISCOVERY = "discovery"

  const val VerifyConnection = "verifyconnection"
  const val RadioStations = "radiostations"

  const val CommandUnavailable = "commandunavailable"

  // Repeat Constants
  const val ONE = "one"
  const val ALL = "All"

  const val PlayerOutput = "playeroutput"
  const val PlayerOutputSwitch = "playeroutputswitch"

  /**
   * Toggle action in protocol. This should be send to the functions with multiple states
   * in order to change to the next in order state.
   */
  const val TOGGLE = "toggle"

  const val ProtocolVersionNumber = 5

  @Retention(AnnotationRetention.SOURCE)
  @StringDef(
    Player,
    ProtocolTag,
    PluginVersion,
    ClientNotAllowed,
    PlayerStatus,
    PlayerRepeat,
    PlayerScrobble,
    PlayerShuffle,
    PlayerMute,
    PlayerPlayPause,
    PlayerPrevious,
    PlayerNext,
    PlayerStop,
    PlayerState,
    PlayerVolume,
    NowPlayingTrack,
    NowPlayingCover,
    NowPlayingPosition,
    NowPlayingLyrics,
    NowPlayingRating,
    NowPlayingLfmRating,
    NowPlayingList,
    NowPlayingListPlay,
    NowPlayingListRemove,
    NowPlayingListMove,
    NowPlayingQueue,
    PING,
    PONG,
    INIT,
    PlayerPlay,
    PlayerPause,
    PlaylistList,
    PlaylistPlay,
    NoBroadcast,
    LibraryBrowseGenres,
    LibraryBrowseArtists,
    LibraryBrowseAlbums,
    LibraryBrowseTracks,
    DISCOVERY,
    VerifyConnection,
    RadioStations,
    CommandUnavailable,
    PlayerOutput,
    PlayerOutputSwitch
  )
  annotation class Context
}