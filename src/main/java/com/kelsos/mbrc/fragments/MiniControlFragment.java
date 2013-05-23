package com.kelsos.mbrc.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.kelsos.mbrc.R;
import roboguice.inject.InjectView;



public class MiniControlFragment extends RoboSherlockFragment {

    @InjectView (R.id.mc_track_cover)
    ImageView trackCover;
    @InjectView (R.id.mc_track_artist)
    TextView trackArtist;
    @InjectView (R.id.mc_track_title)
    TextView trackTitle;
    @InjectView (R.id.mc_next_track)
    ImageButton playNext;
    @InjectView (R.id.mc_play_pause)
    ImageButton playPause;
    @InjectView (R.id.mc_prev_track)
    ImageButton playPrevious;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.ui_fragment_mini_control, container, false);
    }

    public void updateTrackInfo(String artist, String title, Bitmap cover) {
        trackCover.setImageBitmap(cover);
        trackArtist.setText(artist);
        trackTitle.setText(title);
    }
}
