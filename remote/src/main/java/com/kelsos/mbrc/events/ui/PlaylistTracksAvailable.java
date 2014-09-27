package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.dbdata.QueueTrack;

import java.util.List;

public class PlaylistTracksAvailable {
    private List<QueueTrack> playlistTracks;
    private boolean stored;

    public PlaylistTracksAvailable(List<QueueTrack> playlistTracks, boolean stored) {
        this.playlistTracks = playlistTracks;
        this.stored = stored;
    }

    public List<QueueTrack> getPlaylistTracks() {
        return playlistTracks;
    }

    public boolean isStored() {
        return stored;
    }
}
