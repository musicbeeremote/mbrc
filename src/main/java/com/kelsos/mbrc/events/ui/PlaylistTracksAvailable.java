package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.model.TrackEntry;

import java.util.ArrayList;

public class PlaylistTracksAvailable {
    private ArrayList<TrackEntry> playlistTracks;
    private boolean stored;

    public PlaylistTracksAvailable(ArrayList<TrackEntry> playlistTracks, boolean stored) {
        this.playlistTracks = playlistTracks;
        this.stored = stored;
    }

    public ArrayList<TrackEntry> getPlaylistTracks() {
        return playlistTracks;
    }

    public boolean isStored() {
        return stored;
    }
}
