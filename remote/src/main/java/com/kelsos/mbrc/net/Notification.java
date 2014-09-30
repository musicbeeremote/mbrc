package com.kelsos.mbrc.net;

public final class Notification {



    private Notification() { }

    public static final String NOWPLAYING_LIST_CHANGED = "nowplaying-list-changed";
    public static final String AUTODJ_STOPPED = "autodj-stopped";
    public static final String AUTODJ_STARTED = "autodj-started";
    public static final String SCROBBLE_STATUS_CHANGED = "scrobble-status-changed";
    public static final String MUTE_STATUS_CHANGED = "mute-status-changed";
    public static final String POSITION_CHANGED = "position-changed";
    public static final String TRACK_CHANGED = "track-changed";
    public static final String PLAY_STATUS_CHANGED = "play-status-changed";
    public static final String VOLUME_CHANGED = "volume-changed";
    public static final String REPEAT_STATUS_CHANGED = "repeat-status-changed";
    public static final String SHUFFLE_STATUS_CHANGED = "shuffle-status-changed";
    public static final String COVER_CHANGED = "cover-changed";
    public static final String LYRICS_CHANGED = "lyrics-changed";

    public static final String CLIENT_NOT_ALLOWED = "notallowed";
}
