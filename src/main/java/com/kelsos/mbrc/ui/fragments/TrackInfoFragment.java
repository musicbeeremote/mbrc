package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import roboguice.inject.InjectView;

public class TrackInfoFragment extends RoboSherlockFragment {
    @InjectView (R.id.track_title) private TextView trackTitle;
    @InjectView (R.id.track_artist) private TextView trackArtist;
    @InjectView (R.id.track_album) private TextView trackAlbum;
    @InjectView (R.id.track_year) private TextView trackYear;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.ui_main_track_info, container, false);
    }

    @Override public void onStart() {
        super.onStart();

    }

    public void setTrackInfo(TrackInfoChange trackInfo) {
        trackTitle.setText(trackInfo.getTitle());
        trackArtist.setText(trackInfo.getArtist());
        trackAlbum.setText(trackInfo.getAlbum());
        trackYear.setText(trackInfo.getYear());
    }
}
