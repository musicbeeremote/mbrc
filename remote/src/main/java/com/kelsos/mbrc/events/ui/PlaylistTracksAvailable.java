package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.TrackEntry;

import java.util.List;

public class PlaylistTracksAvailable {
    private List<TrackEntry> playlistTracks;
    private boolean stored;

    public PlaylistTracksAvailable(List<TrackEntry> playlistTracks, boolean stored) {
        this.playlistTracks = playlistTracks;
        this.stored = stored;
    }

    public List<TrackEntry> getPlaylistTracks() {
        return playlistTracks;
    }

    public boolean isStored() {
        return stored;
    }
}
