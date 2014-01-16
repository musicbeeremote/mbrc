package com.kelsos.mbrc.net;

public final class Protocol {

    private Protocol() { }

    public static final String ERROR = "error";
    public static final String PLAYER = "player";
    public static final String PROTOCOL = "protocol";
    public static final String PLAYER_NAME = "MusicBee";
    public static final String PROTOCOL_VERSION = "2";
    public static final String PLUGIN_VERSION = "pluginversion";
    public static final String CLIENT_NOT_ALLOWED = "notallowed";

    public static final String PLAYER_STATUS = "playerstatus";
    public static final String PLAYER_REPEAT = "playerrepeat";
    public static final String PLAYER_SCROBBLE = "scrobbler";
    public static final String PLAYER_SHUFFLE = "playershuffle";
    public static final String PLAYER_MUTE = "playermute";
    public static final String PLAYER_PLAY_PAUSE = "playerplaypause";
    public static final String PLAYER_PREVIOUS = "playerprevious";
    public static final String PLAYER_NEXT = "playernext";
    public static final String PLAYER_STOP = "playerstop";
    public static final String PLAYER_STATE = "playerstate";
    public static final String PLAYER_VOLUME = "playervolume";
    public static final String PLAYER_AUTO_DJ = "playerautodj";

    public static final String NOW_PLAYING_TRACK = "nowplayingtrack";
    public static final String NOW_PLAYING_COVER = "nowplayingcover";
    public static final String NOW_PLAYING_POSITION = "nowplayingposition";
    public static final String NOW_PLAYING_LYRICS = "nowplayinglyrics";
    public static final String NOW_PLAYING_RATING = "nowplayingrating";
    public static final String NOW_PLAYING_LFM_RATING = "nowplayinglfmrating";
    public static final String NOW_PLAYING_LIST = "nowplayinglist";
    public static final String NOW_PLAYING_LIST_CHANGED = "nowplayinglistchanged";
    public static final String NOW_PLAYING_PLAY = "nowplayinglistplay";
    public static final String NOW_PLAYING_REMOVE = "nowplayinglistremove";
    public static final String NOW_PLAYING_MOVE = "nowplayinglistmove";
    public static final String NOW_PLAYING_LIST_SEARCH = "nowplayinglistsearch";

    public static final String LIBRARY_SEARCH_ARTIST = "librarysearchartist";
    public static final String LIBRARY_SEARCH_ALBUM = "librarysearchalbum";
    public static final String LIBRARY_SEARCH_GENRE = "librarysearchgenre";
    public static final String LIBRARY_SEARCH_TITLE = "librarysearchtitle";

    public static final String LIBRARY_ARTIST_ALBUMS = "libraryartistalbums";
    public static final String LIBRARY_GENRE_ARTISTS = "librarygenreartists";
    public static final String LIBRARY_ALBUM_TRACKS = "libraryalbumtracks";

    public static final String LIBRARY_QUEUE_GENRE = "libraryqueuegenre";
    public static final String LIBRARY_QUEUE_ARTIST = "libraryqueueartist";
    public static final String LIBRARY_QUEUE_ALBUM = "libraryqueuealbum";
    public static final String LIBRARY_QUEUE_TRACK = "libraryqueuetrack";

    public static final String PLAYLIST_LIST = "playlistlist";
    public static final String PLAYLIST_GET_FILES = "playlistgetfiles";
    public static final String PLAYLIST_PLAY_NOW = "playlistplaynow";
    public static final String PLAYLIST_REMOVE = "playlistremove";
    public static final String PLAYLIST_MOVE = "playlistmove";
    public static final String PLAYLIST_ADD_FILES = "playlistaddfiles";
    public static final String PLAYLIST_CREATE = "playlistcreate";

    public static final String LIBRARY_SYNC = "librarysync";

    public static final String REQUEST = "req";
    public static final String REPLY = "rep";
    public static final String MESSAGE = "msg";

}
