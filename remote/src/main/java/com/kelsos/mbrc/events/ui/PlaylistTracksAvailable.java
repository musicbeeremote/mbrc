package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.dbdata.NowPlayingTrack;

import java.util.List;

public class PlaylistTracksAvailable {
    private List<NowPlayingTrack> playlistTracks;
    private boolean stored;

    public PlaylistTracksAvailable(List<NowPlayingTrack> playlistTracks, boolean stored) {
        this.playlistTracks = playlistTracks;
        this.stored = stored;
    }

    public List<NowPlayingTrack> getPlaylistTracks() {
        return playlistTracks;
    }

    public boolean isStored() {
        return stored;
    }
}
