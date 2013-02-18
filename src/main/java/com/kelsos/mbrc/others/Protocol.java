package com.kelsos.mbrc.others;

public class Protocol {

    public static final String PLAYPAUSE = "playPause";
    public static final String PREVIOUS = "previous";
    public static final String NEXT = "next";
    public static final String STOP = "stopPlayback";
    public static final String PLAYSTATE = "playState";
    public static final String VOLUME = "volume";
    public static final String SONGCHANGED = "songChanged";
    public static final String SONGINFO = "songInfo";
    public static final String SONGCOVER = "songCover";
    public static final String SHUFFLE = "shuffle";
    public static final String MUTE = "mute";
    public static final String REPEAT = "repeat";
    public static final String PLAYLIST = "playlist";
    public static final String PLAYNOW = "playNow";
    public static final String SCROBBLE = "scrobbler";
    public static final String LYRICS = "lyrics";
    public static final String RATING = "rating";
    public static final String PLAYER_STATUS = "playerStatus";
    public static final String ERROR = "error";
    public static final String DATA = "data";
    public static final String PLAYER = "player";
    public static final String PROTOCOL = "protocol";

    /** Protocol 1.2 **/

	public static final String PLAYNOW_REMOVESELECTED = "playNowRemoveSelected";
    public static final String PLAYBACK_POSITION = "playbackPosition";
	public static final String NOT_ALLOWED = "notAllowed";

	/** Protocol 1.3 **/

	public static final double CLIENT_PROTOCOL_VERSION = 1.3;
    public static final String NOW_PLAYING_CHANGED = "nowplayingchanged";
    public static final String MOVE_TRACK = "nowplayingmove";
    public static final String LFM_LOVE = "lfmlove";
    public static final String LFM_BAN = "lfmban";
    public static final String LIB_SEARCH = "libsearch";
    public static final String NOW_PLAYING_SEARCH = "nowplayingsearch";
}